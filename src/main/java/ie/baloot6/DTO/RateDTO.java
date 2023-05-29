package ie.baloot6.DTO;

public class RateDTO {
    private final double rating;
    private final String status;
    private final long rateCount;

    public double getRating() {
        return rating;
    }

    public String getStatus() {
        return status;
    }


    public long getRateCount() {
        return rateCount;
    }
    public RateDTO(String status, double rating, long commodityRateCount) {
        this.rating = rating;
        this.status = status;
        this.rateCount = commodityRateCount;
    }
}
