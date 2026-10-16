import java.util.regex.Pattern;

public abstract class Parcel {
    public static final Pattern TRACKING_PATTERN = Pattern.compile("^[A-Z]{3}\\d{4}$");

    protected final String trackingNumber;
    protected final String receiverName;
    protected final double weight;
    protected DeliveryStrategy deliveryService;
    protected String status = "Pending";

    public Parcel(String trackingNumber, String receiverName, double weight, DeliveryStrategy deliveryService)
            throws ParcelException {
        if (trackingNumber == null || !TRACKING_PATTERN.matcher(trackingNumber.trim().toUpperCase()).matches()) {
            throw new ParcelException("Invalid Tracking Number! Must be 3 letters followed by 4 digits (e.g. ABC0001).");
        }
        if (receiverName == null || receiverName.trim().isEmpty()) {
            throw new ParcelException("Receiver name cannot be empty!");
        }
        if (receiverName.matches(".*\\d.*") && receiverName.matches(".*[^a-zA-Z0-9\\s].*")) {
            throw new ParcelException("Receiver name cannot contain numbers or special characters!");
        }
        if (receiverName.matches(".*\\d.*")) {
            throw new ParcelException("Receiver name cannot contain numbers!");
        }
        if (receiverName.matches(".*[^a-zA-Z\\s].*")) {
            throw new ParcelException("Receiver name cannot contain special characters!");
        }
        if (weight <= 0) {
            throw new ParcelException("Weight must be greater than 0 kg!");
        }
        if (deliveryService == null) {
            throw new ParcelException("Delivery service cannot be null!");
        }

        this.trackingNumber = trackingNumber.trim().toUpperCase();
        this.receiverName = receiverName.trim();
        this.weight = weight;
        this.deliveryService = deliveryService;
    }

    public abstract String getType();
    public abstract double getChargeableWeight();
    public abstract double calculateBaseFee();

    public double calculateDeliveryFee() {
        return deliveryService.calculateFee(calculateBaseFee());
    }

    public void changeDeliveryService(DeliveryStrategy newService) throws ParcelException {
        if (isDelivered()) {
            throw new ParcelException("Parcel already delivered!");
        }
        if (newService == null) {
            throw new ParcelException("Invalid delivery service.");
        }
        newService.validate(getChargeableWeight());
        this.deliveryService = newService;
    }

    public void markDelivered() throws ParcelException {
        if (isDelivered()) {
            throw new ParcelException("Parcel was already delivered.");
        }
        this.status = "Delivered";
    }

    public boolean isDelivered() {
        return "Delivered".equalsIgnoreCase(status);
    }

    public String getTrackingNumber() { return trackingNumber; }
    public String getReceiverName() { return receiverName; }
    public double getWeight() { return weight; }
    public DeliveryStrategy getDeliveryService() { return deliveryService; }
    public String getStatus() { return status; }

    public String getFormattedChargeableWeight() {
        double cw = getChargeableWeight();
        return cw == (long) cw ? String.format("%d kg", (long) cw) : String.format("%.2f kg", cw);
    }
}
