package ie.baloot6.data;

import ie.baloot6.DTO.CommodityDTO;
import ie.baloot6.exception.InvalidIdException;
import ie.baloot6.exception.InvalidValueException;
import ie.baloot6.exception.NotEnoughAmountException;
import ie.baloot6.model.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;


public interface IRepository {
    void getData(@NotNull String apiUri) throws InvalidIdException, ParseException;

    void addComment(@NotNull String username, long commodityId, @NotNull String commentText) throws IllegalArgumentException;

    void addUser(String username, String password, String email, Date birthDate, String address, long credit) throws InvalidIdException;

    User getUserByUsername(@NotNull String username);

    void addProvider(long providerId, @NotNull String name, Date registryDate) throws InvalidIdException;

    Optional<Provider> getProvider(long id);

    void addCommodity(long commodityId, String name, long providerId, long price, long inStock, List<String> categories) throws InvalidIdException;

    void addDiscount(@NotNull String discountCode, int discountAmount);

    List<Commodity> getCommodityList();

    List<Commodity> getProvidersCommoditiesList(long providerId);

    Optional<Commodity> getCommodityById(long id);

    List<Commodity> getCommoditiesByCategory(@NotNull String category);

    List<Commodity> getCommoditiesByName(@NotNull String name);

    List<Commodity> searchCommoditiesByName(@NotNull String name);

    void addToBuyList(@NotNull String username, long commodityId, long count) throws NotEnoughAmountException, InvalidIdException;

    void removeFromBuyList(@NotNull String username, long commodityId, long count) throws NotEnoughAmountException;

    double addRating(@NotNull String username, long commodityId, double rate);

    void addVote(@NotNull String voter, long commentId, int vote) throws InvalidIdException, InvalidValueException;

    long getLikes(long commentId);

    long getDislikes(long commentId);

    List<Comment> getCommentsForCommodity(long commodityId);

    void addCredit(String username, long credit) throws InvalidIdException, InvalidValueException;

    void purchase(@NotNull String username, float discount) throws NotEnoughAmountException, InvalidIdException;

    Optional<Double> getCommodityRating(long commodityId);

    Optional<Double> getUserRating(@NotNull String username, long commodityId);

    long calculateTotalBuyListPrice(String username) throws InvalidIdException;

    List<Commodity> getShoppingList(String username) throws InvalidIdException;

    long getInShoppingListCount(String username, long commodityId);
    long getInPurchasedListCount(String username, long commodityId);

    List<ShoppingItem> getPurchasedList(String username) throws InvalidIdException;

    Optional<Discount> getDiscount(String discountCode);

    boolean hasUserUsedDiscount(String discountCode, String username) throws InvalidIdException;

    List<Commodity> getRecommendedCommodities(String username, long commodityId);

    Optional<Comment> getComment(long commentId);

    boolean authenticate(String username, String password);

    long getCommodityRateCount(long commodityId);

    int getUserVoteForComment(String username, long commentId);

    void addCategory(String categoryName);
}

