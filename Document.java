public class Document extends Parcel {
    public static final double MAX_WEIGHT = 0.5;
    public static final double BASE_FEE = 50.0;

    private final int numberOfPages;

    public Document(String trackingNumber, String receiverName, double weight,
                    DeliveryStrategy deliveryService, int numberOfPages) throws ParcelException {
        super(trackingNumber, receiverName, weight, deliveryService);

        if (numberOfPages < 1 || numberOfPages > 100) {
            throw new ParcelException("Number of pages must be between 1 and 100!");
        }
        if (weight > MAX_WEIGHT) {
            throw new ParcelException(String.format("Document weight cannot exceed %.1f kg! Entered: %.2f kg", MAX_WEIGHT, weight));
        }

        this.numberOfPages = numberOfPages;
        this.deliveryService.validate(getChargeableWeight());
    }

    public int getNumberOfPages() { return numberOfPages; }

    @Override public String getType() { return "Document"; }
    @Override public double getChargeableWeight() { return weight; }
    @Override public double calculateBaseFee() { return BASE_FEE; }
}
