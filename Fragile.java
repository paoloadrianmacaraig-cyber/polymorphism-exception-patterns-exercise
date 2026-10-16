public class Fragile extends Box {
    public static final double FRAGILE_MAX_WEIGHT = 30.0;
    public static final double HANDLING_FEE = 100.0;

    private final String handlingNote;

    public Fragile(String trackingNumber, String receiverName, double weight,
                   DeliveryStrategy deliveryService, double length, double width, double height,
                   String handlingNote) throws ParcelException {
        super(trackingNumber, receiverName, weight, deliveryService, length, width, height);

        if (weight > FRAGILE_MAX_WEIGHT) {
            throw new ParcelException(String.format("Fragile weight cannot exceed %.1f kg! Entered: %.2f kg", FRAGILE_MAX_WEIGHT, weight));
        }
        if (handlingNote == null || handlingNote.trim().isEmpty()) {
            throw new ParcelException("Handling note cannot be empty!");
        }

        this.handlingNote = handlingNote.trim();
    }

    public String getHandlingNote() { return handlingNote; }

    @Override public String getType() { return "Fragile"; }

    @Override
    public double calculateBaseFee() {
        return super.calculateBaseFee() + HANDLING_FEE;
    }
}
