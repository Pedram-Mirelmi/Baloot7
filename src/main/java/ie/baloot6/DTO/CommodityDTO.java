package ie.baloot6.DTO;

import ie.baloot6.model.Category;
import ie.baloot6.model.Commodity;

import java.util.List;

public class CommodityDTO {
    private long id;
    private String name;
    private long providerId;
    private long price;
    private List<String> categories;
    private float rating;
    private long inStock;
    final private long inCart;
    final private long rateCount;
    final private String providerName;

    public CommodityDTO(Commodity commodity, long inCart) {
        this.id = commodity.getCommodityId();
        this.name = commodity.getName();
        this.providerId = commodity.getProvider().getProviderId();
        this.price = commodity.getPrice();
        this.setCategories(commodity.getCategorySet().stream().map(Category::getCategoryName).toList());
        this.inStock = commodity.getInStock();
        this.inCart = inCart;
        this.rateCount = commodity.getRatings().size();
        commodity.getRatings().forEach(r -> this.rating += r.getRating() / rateCount);
        this.providerName = commodity.getProvider().getName();
    }

    public long getId() {
        return id;
    }

    public long getCommodityId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public String getCommodityName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getProviderId() {
        return providerId;
    }

    public void setProviderId(long providerId) {
        this.providerId = providerId;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public long getInStock() {
        return inStock;
    }

    public void setInStock(long inStock) {
        this.inStock = inStock;
    }

    public long getInCart() {
        return inCart;
    }

    public long getCount() {
        return inCart;
    }

    public long getRateCount() {
        return rateCount;
    }

    public String getProviderName() {
        return providerName;
    }
}
