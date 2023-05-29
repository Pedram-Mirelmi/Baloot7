package ie.baloot6.DJO;

import com.google.gson.annotations.SerializedName;

public class DiscountDJO {
    @SerializedName("discountCode")
    private String discountCode;

    @SerializedName("discount")
    private int discount;

    public DiscountDJO(String discountCode, int discount) {
        this.discountCode = discountCode;
        this.discount = discount;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
