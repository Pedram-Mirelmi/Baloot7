package ie.baloot7.model;

import jakarta.persistence.*;

@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long discountId;

    @Column(nullable = false)
    private String discountCode;

    @Column(nullable = false)
    private int discountAmount;

    public Discount(String discountCode, int discountAmount) {
        this.discountCode = discountCode;
        this.discountAmount = discountAmount;
    }

    public Discount() {

    }

    public long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(long discountId) {
        this.discountId = discountId;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discount) {
        this.discountAmount = discount;
    }
}
