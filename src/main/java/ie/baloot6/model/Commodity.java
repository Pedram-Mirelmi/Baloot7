package ie.baloot6.model;


import com.google.gson.annotations.SerializedName;
import ie.baloot6.exception.NotEnoughAmountException;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "commodities")
public class Commodity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long commodityId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "providerId")
    private Provider provider;

    @Column(nullable = false)
    private long price;

    @Column(nullable = false)
    private long inStock;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            joinColumns = { @JoinColumn(name = "commodityId") },
            inverseJoinColumns = { @JoinColumn(name = "categoryId") }
    )
    Set<Category> categorySet = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name = "commodityId")
    private Set<Rating> ratings = new HashSet<>();


    public Commodity(String name, Provider provider, long price, long inStock) {
        this.provider = provider;
        this.name = name;
        this.price = price;
        this.inStock = inStock;
    }

//    public Commodity(Commodity commodity) {
//        this(commodity.name, commodity.getProvider(), commodity.price, commodity.getInStock());
//        this.commodityId = commodity.getCommodityId();
//        categorySet = commodity.getCategorySet();
//    }

    public Set<Category> getCategorySet() {
        return categorySet;
    }

    public Commodity() {

    }

    public long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(long commodityId) {
        this.commodityId = commodityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getInStock() {
        return inStock;
    }

    public void setInStock(long inStock) {
        this.inStock = inStock;
    }

    public void addStuck(long newAmount) {
        this.inStock += newAmount;
    }

    public void subtractStock(long amount) throws NotEnoughAmountException {
        if(inStock < amount) {
            throw new NotEnoughAmountException("Not enough in stock");
        }
        setInStock(getInStock() - amount);
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Set<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(Set<Rating> ratings) {
        this.ratings = ratings;
    }

    public void setCategorySet(Set<Category> categorySet) {
        this.categorySet = categorySet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commodity commodity = (Commodity) o;
        return commodityId == commodity.commodityId;
    }
}