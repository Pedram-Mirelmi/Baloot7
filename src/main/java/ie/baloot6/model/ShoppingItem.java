package ie.baloot6.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "shoppingItems")
public class ShoppingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long shoppingItemId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "commodityId", nullable = false)
    private Commodity commodity;

    @Column(nullable = false)
    private long count;

    public boolean isBeenPurchased() {
        return beenPurchased;
    }

    public void setBeenPurchased(boolean beenPurchased) {
        this.beenPurchased = beenPurchased;
    }

    @Column(nullable = false)
    private boolean beenPurchased;

    public ShoppingItem(User user, Commodity commodity, long count, boolean beenPurchased) {
        this.user = user;
        this.commodity = commodity;
        this.count = count;
        this.beenPurchased = beenPurchased;
    }

    public ShoppingItem() {

    }

    public long getShoppingItemId() {
        return shoppingItemId;
    }

    public void setShoppingItemId(long shoppingItemId) {
        this.shoppingItemId = shoppingItemId;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
