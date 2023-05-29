package ie.baloot6.service;

import com.google.gson.Gson;
import ie.baloot6.DJO.*;
import ie.baloot6.DTO.CommodityDTO;
import ie.baloot6.data.IRepository;
import ie.baloot6.data.StaticData;
import ie.baloot6.exception.InvalidIdException;
import ie.baloot6.exception.InvalidValueException;
import ie.baloot6.exception.NotEnoughAmountException;
import ie.baloot6.model.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class Repository implements IRepository {

    private final EntityManagerFactory entityManagerFactory;

    public Repository() throws ParseException {
        var registry = new StandardServiceRegistryBuilder().configure().build();
//        entityManagerFactory = Persistence.createEntityManagerFactory("default");
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        long commodityCount = (long) entityManager.createQuery("SELECT count(c) from Commodity c").getSingleResult();
        if (commodityCount == 0)
            getData("http://5.253.25.110:5000/api/");
    }

    private String getResource(@NotNull String uri) {
        if (uri.equals("http://5.253.25.110:5000/api/users"))
            return StaticData.usersString;
        if (uri.equals("http://5.253.25.110:5000/api/commodities"))
            return StaticData.commoditiesString;
        if (uri.equals("http://5.253.25.110:5000/api/providers"))
            return StaticData.providersString;
        if (uri.equals("http://5.253.25.110:5000/api/comments"))
            return StaticData.commentsString;
        if (uri.equals("http://5.253.25.110:5000/api/discount"))
            return StaticData.discounts;
        return null;
//        try {
//            URL obj = new URL(uri);
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//            con.setRequestMethod("GET");
//            int responseCode = con.getResponseCode();
//            if (responseCode != 200)
//                throw new IOException("foreign api sent a response with status code " + responseCode);
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            StringBuilder sb = new StringBuilder();
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                sb.append(inputLine);
//            }
//            return sb.toString();
//        } catch (IOException e) {
//            System.out.println("Exception occurred while getting data from foreign api:" + e.getMessage());
//            exit(1);
//        }
//        return null;
    }

    @Override
    public void getData(@NotNull String apiUri) throws InvalidIdException, ParseException {
        Gson gson = new Gson();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        UserDJO[] usersList = gson.fromJson(getResource(apiUri + "users"), UserDJO[].class);
        for (UserDJO user : usersList) {
            try {
                addUser(user.getUsername(), user.getPassword(), user.getEmail(), new Date(formatter.parse(user.getBirthDate()).getTime()), user.getAddress(), user.getCredit());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        ProviderDJO[] providersList = gson.fromJson(getResource(apiUri + "providers"), ProviderDJO[].class);
        for (ProviderDJO provider : providersList) {

            try {
                addProvider(provider.getId(), provider.getName(), new Date(formatter.parse(provider.getRegistryDate()).getTime()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        CommodityDJO[] commoditiesList = gson.fromJson(getResource(apiUri + "commodities"), CommodityDJO[].class);
        for (CommodityDJO commodity : commoditiesList) {
            try {
                addCommodity(commodity.getId(), commodity.getName(), commodity.getProviderId(), commodity.getPrice(), commodity.getInStock(), commodity.getCategories());
                for (var category : commodity.getCategories()) {

                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }


        CommentDJO[] commentsList = gson.fromJson(getResource(apiUri + "comments"), CommentDJO[].class);
        for (int i = 0; i < commentsList.length; i++) {
            try {

                commentsList[i].setCommentId(i + 1L);
                commentsList[i].setUsername(getUsernameFromEmail(commentsList[i].getUserEmail()));
                addComment(commentsList[i].getCommentId(),
                        commentsList[i].getUsername(),
                        commentsList[i].getCommodityId(),
                        new Date(formatter.parse(commentsList[i].getDate()).getTime()),
                        commentsList[i].getText());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        DiscountDJO[] discounts = gson.fromJson(getResource(apiUri + "discount"), DiscountDJO[].class);
        for (DiscountDJO discount : discounts) {
            try {
                addDiscount(discount.getDiscountCode(), discount.getDiscount());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void addComment(long commentId, @NotNull String username, long commodityId, @NotNull Date date, @NotNull String commentText) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = getUser(username, entityManager);
        Commodity commodity = getCommodity(commodityId, entityManager);
        entityManager.persist(new Comment(commentId, user, commodity, commentText, date));

        entityManager.getTransaction().commit();
    }

    @Override
    public void addComment(@NotNull String username, long commodityId, @NotNull String commentText) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = getUser(username, entityManager);
        Commodity commodity = getCommodity(commodityId, entityManager);
        entityManager.persist(new Comment(user, commodity, commentText, new Date(System.currentTimeMillis())));

        entityManager.getTransaction().commit();
    }

    @Override
    public void addUser(String username, String password, String email, Date birthDate, String address, long credit) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try {
            User user = new User(username, password, email, birthDate, address, credit);
            entityManager.persist(user);
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new InvalidIdException("Duplicated username");
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public User getUserByUsername(@NotNull String username) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return getUser(username, entityManager);
    }


    @Override
    public void addProvider(long providerId, @NotNull String name, Date registryDate) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Provider provider = new Provider(name, registryDate);
        provider.setProviderId(providerId);
        entityManager.persist(provider);

        entityManager.getTransaction().commit();
    }

    @Override
    public Optional<Provider> getProvider(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Provider provider = entityManager.find(Provider.class, id);

        entityManager.getTransaction().commit();
        return Optional.ofNullable(provider);
    }

    @Override
    public void addCommodity(long commodityId, String name, long providerId, long price, long inStock, List<String> categories) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Provider provider = entityManager.find(Provider.class, providerId);
        if (provider == null) {
            entityManager.getTransaction().rollback();
            throw new InvalidIdException("Invalid provider id");
        }

        Commodity commodity = new Commodity(name, provider, price, inStock);

        for (var categoryName : categories) {
            var categoryObj = getCategoryByName(categoryName, entityManager);
            if (categoryObj.isEmpty()) {
                categoryObj = Optional.of(new Category(categoryName));
            }
            categoryObj.get().getCommoditySet().add(commodity);
            commodity.getCategorySet().add(categoryObj.get());
        }

        entityManager.persist(commodity);

        entityManager.getTransaction().commit();
    }


    @Override
    public void addDiscount(@NotNull String discountCode, int discountAmount) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(new Discount(discountCode, discountAmount));
        entityManager.getTransaction().commit();
    }

    @Override
    public List<Commodity> getCommodityList() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var resultList = entityManager.createQuery("SELECT c from Commodity c").getResultList();

        return resultList;
    }

    @Override
    public List<Commodity> getProvidersCommoditiesList(long providerId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var resultList = entityManager.createQuery("SELECT c from Commodity c where c.provider.providerId=:providerId")
                .setParameter("providerId", providerId)
                .getResultList();

        return resultList;
    }

    @Override
    public Optional<Commodity> getCommodityById(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var commodity = entityManager.find(Commodity.class, id);

        return Optional.ofNullable(commodity);
    }

    @Override
    public List<Commodity> getCommoditiesByCategory(@NotNull String categoryName) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Optional<Category> category = getCategoryByName(categoryName, entityManager);
        if (category.isEmpty()) {
            return new ArrayList<>();
        }

        var commodities = category.get().getCommoditySet().stream().toList();
        entityManager.getTransaction().commit();
        return category.get().getCommoditySet().stream().toList();
    }

    @Override
    public List<Commodity> getCommoditiesByName(@NotNull String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var resultList = entityManager.createQuery("select c from Commodity c where c.name=:name")
                .setParameter("name", name)
                .getResultList();

        return resultList;
    }

    @Override
    public List<Commodity> searchCommoditiesByName(@NotNull String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var resultList = entityManager.createQuery("select c from Commodity c where c.name like :name")
                .setParameter("name", "%" + name + "%")
                .getResultList();

        return resultList;
    }

    @Override
    public void addToBuyList(@NotNull String username, long commodityId, long count) throws NotEnoughAmountException, InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var user = getUser(username, entityManager);
        Commodity commodity = getCommodity(commodityId, entityManager);

        var resultList = entityManager.createQuery("select si from ShoppingItem si " +
                        "                                             where si.user.userId=:userId " +
                        "                                                  and si.beenPurchased=false " +
                        "                                                   and si.commodity.commodityId=:commodityId")
                .setParameter("userId", user.getUserId())
                .setParameter("commodityId", commodityId)
                .getResultList();

        ShoppingItem shoppingItem;
        if (resultList.isEmpty()) {
            shoppingItem = new ShoppingItem(user, commodity, count, false);
            entityManager.persist(shoppingItem);
        } else {
            shoppingItem = (ShoppingItem) resultList.get(0);
            shoppingItem.setCount(shoppingItem.getCount() + count);
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public void removeFromBuyList(@NotNull String username, long commodityId, long count) throws NotEnoughAmountException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = getUser(username, entityManager);
        Commodity commodity = getCommodity(commodityId, entityManager);

        var resultList = entityManager.createQuery("select si from ShoppingItem si " +
                        " where si.user.userId=:userId " +
                        " and si.beenPurchased=false " +
                        " and si.commodity.commodityId=:commodityId")
                .setParameter("userId", user.getUserId())
                .setParameter("commodityId", commodityId)
                .getResultList();

        ShoppingItem shoppingItem;
        if (resultList.isEmpty()) {
            throw new NotEnoughAmountException("No shopping Item to remove from");
        } else {
            shoppingItem = (ShoppingItem) resultList.get(0);
            if (shoppingItem.getCount() >= count) {
                shoppingItem.setCount(shoppingItem.getCount() - count);
            } else {
                shoppingItem.setCount(0);
            }
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public double addRating(@NotNull String username, long commodityId, double rate) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = getUser(username, entityManager);
        Commodity commodity = getCommodity(commodityId, entityManager);
        Rating rating;

        var resultList = entityManager.createQuery("select r from Rating r where user.userId=:userId and" +
                        " commodity.commodityId=:commodityId")
                .setParameter("userId", user.getUserId())
                .setParameter("commodityId", commodityId)
                .getResultList();
        if (resultList.isEmpty()) {
            rating = new Rating(user, commodity, rate);
            entityManager.persist(rating);
        } else {
            rating = (Rating) resultList.get(0);
            rating.setRating(rate);
        }

        entityManager.getTransaction().commit();
        return getCommodityRating(commodityId).get();
    }

    @Override
    public void addVote(@NotNull String voter, long commentId, int voteValue) throws InvalidIdException, InvalidValueException {
        if (voteValue < -1 || 1 < voteValue) {
            throw new InvalidValueException("Invalid vote value");
        }
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUser(voter, entityManager);
        Comment comment = getComment(commentId, entityManager);

        var resultList = entityManager.createQuery("select v from Vote v where v.user.userId=:userId and" +
                        " v.comment.commentId=:commentId")
                .setParameter("userId", user.getUserId())
                .setParameter("commentId", commentId)
                .getResultList();
        Vote vote;
        if (resultList.isEmpty()) {
            vote = new Vote(comment, user, voteValue);
            entityManager.persist(vote);
        } else {
            vote = (Vote) resultList.get(0);
            vote.setVote(voteValue);
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public long getLikes(long commentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var resultList = entityManager.createQuery("select sum(v.vote) from Vote v" +
                        " where v.comment.commentId=:commentId and v.vote=1")
                .setParameter("commentId", commentId)
                .getResultList();
        return resultList.isEmpty() || resultList.get(0) == null ? 0 : (Long) resultList.get(0);
    }

    @Override
    public long getDislikes(long commentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var resultList = entityManager.createQuery("select sum(v.vote) from Vote v" +
                        " where v.comment.commentId=:commentId and v.vote=-1")
                .setParameter("commentId", commentId)
                .getResultList();
        return resultList.isEmpty() || resultList.get(0) == null ? 0 : (Long) resultList.get(0);
    }

    @Override
    public List<Comment> getCommentsForCommodity(long commodityId) {
        return entityManagerFactory.createEntityManager().createQuery("select c from Comment c where c.commodity.commodityId=:commodityId")
                .setParameter("commodityId", commodityId)
                .getResultList();
    }

    @Override
    public void addCredit(String username, long credit) throws InvalidIdException, InvalidValueException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = getUser(username, entityManager);
        user.setCredit(user.getCredit() + credit);

        entityManager.getTransaction().commit();
    }

    @Override
    public void purchase(@NotNull String username, float discount) throws NotEnoughAmountException, InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = getUser(username, entityManager);
        var shoppingItems = (List<ShoppingItem>) entityManager.createQuery("select si from ShoppingItem si " +
                        "where user.userId=:userId and si.beenPurchased=false ")
                .setParameter("userId", user.getUserId())
                .getResultList();

        long price = 0;
        for (var shoppingItem : shoppingItems) {
            price += shoppingItem.getCommodity().getPrice() * shoppingItem.getCount();
            if (shoppingItem.getCommodity().getInStock() < shoppingItem.getCount()) {
                entityManager.getTransaction().rollback();
                throw new NotEnoughAmountException("Not enough in stock");
            }
        }
        price = (long) (price * discount);

        if (price > user.getCredit()) {
            entityManager.getTransaction().rollback();
            throw new NotEnoughAmountException("Not enough credit");
        } else {
            user.setCredit(user.getCredit() - price);
            for (var shoppingItem : shoppingItems) {
                shoppingItem.setBeenPurchased(true);
                shoppingItem.getCommodity().subtractStock(shoppingItem.getCount());
            }
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public Optional<Double> getCommodityRating(long commodityId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        return Optional.ofNullable((Double) entityManager.createQuery("select avg(r.rating) from Rating r " +
                        "where r.commodity.commodityId=:commodityId")
                .setParameter("commodityId", commodityId)
                .getSingleResult());
    }

    @Override
    public Optional<Double> getUserRating(@NotNull String username, long commodityId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        var resultList = entityManager.createQuery("select r.rating from Rating r " +
                        "where r.commodity.commodityId=:commodityId and user.userId=:userId")
                .setParameter("commodityId", commodityId)
                .setParameter("userId", user.getUserId())
                .getResultList();

        return resultList.isEmpty() || resultList.get(0) == null ? Optional.empty() : Optional.of((Double) resultList.get(0));
    }

    @Override
    public long calculateTotalBuyListPrice(String username) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        return (Long) entityManager.createQuery(
                        "select sum (si.count * si.commodity.price) " +
                                "from ShoppingItem si " +
                                "where si.user.userId=:userId and si.beenPurchased=false ")
                .setParameter("userId", user.getUserId())
                .getSingleResult();
    }

    @Override
    public List<Commodity> getShoppingList(String username) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        List<Commodity> result = entityManager.createQuery(
                        "select si from ShoppingItem si where si.user.userId=:userId and si.beenPurchased=false")
                .setParameter("userId", user.getUserId())
                .getResultStream().map(si -> ((ShoppingItem) si).getCommodity()).toList();

        return result;
    }

    @Override
    public long getInShoppingListCount(String username, long commodityId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        var resultList = entityManager.createQuery(
                        "select si.count from ShoppingItem si where si.user.userId=:userId and si.commodity.commodityId=:commodityId and si.beenPurchased=false ")
                .setParameter("userId", user.getUserId())
                .setParameter("commodityId", commodityId)
                .getResultList();
        return resultList.isEmpty() || resultList.get(0) == null ? 0 : (Long) resultList.get(0);
    }

    @Override
    public long getInPurchasedListCount(String username, long commodityId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        var resultList = entityManager.createQuery(
                        "select si.count from ShoppingItem si where si.user.userId=:userId and si.commodity.commodityId=:commodityId and si.beenPurchased=true ")
                .setParameter("userId", user.getUserId())
                .setParameter("commodityId", commodityId)
                .getResultList();
        return resultList.isEmpty() || resultList.get(0) == null ? 0 : (Long) resultList.get(0);
    }

    @Override
    public List<ShoppingItem> getPurchasedList(String username) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        return entityManager.createQuery(
                        "select si from ShoppingItem si where si.user.userId=:userId and si.beenPurchased=true")
                .setParameter("userId", user.getUserId())
                .getResultList();
    }

    @Override
    public Optional<Discount> getDiscount(String discountCode) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return Optional.of(getDiscount(discountCode, entityManager));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean hasUserUsedDiscount(String discountCode, String username) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);
        Discount discount = getDiscount(discountCode, entityManager);

        return !entityManager.createNativeQuery(
                        "select * from discountUses " +
                                "where discountId = :discountId and userId = :userId")
                .setParameter("discountId", discount.getDiscountId())
                .setParameter("userId", user.getUserId())
                .getResultList().isEmpty();


    }

    @Override
    public List<Commodity> getRecommendedCommodities(String username, long commodityId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        HashMap<Long, Double> scores = new HashMap<>();
        Commodity commodity = getCommodity(commodityId, entityManager);

        var commodities = (List<Commodity>) entityManager.createQuery("select c from Commodity c").getResultList();

        commodities.forEach(c -> {
            var commonCategories = new HashSet<>(commodity.getCategorySet());
            commonCategories.addAll(c.getCategorySet());
            // todo fix rating
            scores.put(c.getCommodityId(), 11.0 * commonCategories.size());//getUserRating(username, c.getCommodityId()).orElse(null));
        });
        scores.remove(commodityId);
        return scores.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(
                cid -> getCommodity(cid.getKey(), entityManager)
        ).toList().subList(0, 5);

    }

    @Override
    public Optional<Comment> getComment(long commentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return Optional.of(getComment(commentId, entityManager));
    }

    @Override
    public boolean authenticate(String username, String password) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            User user = getUser(username, entityManager);
            if (user.getPassword().equals(password)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    @Override
    public long getCommodityRateCount(long commodityId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return (Long) entityManager.createQuery(
                        "select count (*) from Rating r " +
                                "where r.commodity.commodityId=:commodityId")
                .setParameter("commodityId", commodityId)
                .getSingleResult();
    }

    @Override
    public int getUserVoteForComment(String username, long commentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        var resultList = entityManager.createQuery(
                        "select v.vote from Vote v " +
                                "where v.user.userId=:userId " +
                                "and v.comment.commentId=:commentId")
                .setParameter("userId", user.getUserId())
                .setParameter("commentId", commentId)
                .getResultList();

        return resultList.isEmpty() ? 0 : (Integer) resultList.get(0);
    }

    @Override
    public void addCategory(String categoryName) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        entityManager.persist(new Category(categoryName));

        entityManager.getTransaction().commit();

    }

    private Optional<Category> getCategoryByName(@NotNull String categoryName, EntityManager entityManager) {
        var resultList = entityManager.createQuery("select c from Category c where c.categoryName=:categoryName")
                .setParameter("categoryName", categoryName).getResultList();

        return resultList.size() == 1 ?
                Optional.ofNullable((Category) resultList.get(0)) :
                Optional.empty();
    }

    @NotNull
    private User getUser(@NotNull String username, EntityManager entityManager) throws InvalidIdException {
        List users = entityManager.createQuery("SELECT u FROM User u where u.username=:username")
                .setParameter("username", username).getResultList();
        if (users.isEmpty()) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new InvalidIdException("Invalid user id");
        }
        return (User) users.get(0);
    }

    @NotNull
    public Commodity getCommodity(long commodityId, EntityManager entityManager) throws InvalidIdException {
        Commodity commodity = entityManager.find(Commodity.class, commodityId);
        if (commodity == null) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new InvalidIdException("Invalid commodity Id");
        }
        return commodity;
    }

    @NotNull
    private Discount getDiscount(@NotNull String discountCode, EntityManager entityManager) throws InvalidIdException {


        var resultList = entityManager.createQuery("select d from Discount d where d.discountCode=:discountCode")
                .setParameter("discountCode", discountCode)
                .getResultList();
        if (resultList.isEmpty()) {
            throw new InvalidIdException("Invalid discount code");
        }

        return (Discount) resultList.get(0);

    }


    @NotNull
    private Comment getComment(long commentId, EntityManager entityManager) throws InvalidIdException {
        Comment comment = entityManager.find(Comment.class, commentId);
        if (comment == null) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new InvalidIdException("Invalid comment Id");
        }
        return comment;
    }

    @NotNull
    private String getUsernameFromEmail(@NotNull String email) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        return (String) entityManager.createQuery("select u.username from User u where u.email=:email")
                .setParameter("email", email)
                .getSingleResult();
    }

}