public class Box extends Parcel {
    public static final double MAX_WEIGHT = 50.0;
    public static final double MAX_DIMENSION = 150.0;
    public static final double RATE_PER_KG = 40.0;
    public static final double VOLUMETRIC_DIVISOR = 3500.0;

    protected final double length;
    protected final double width;
    protected final double height;

    public Box(String trackingNumber, String receiverName, double weight,
               DeliveryStrategy deliveryService, double length, double width, double height) throws ParcelException {
        super(trackingNumber, receiverName, weight, deliveryService);

        if (length <= 0 || width <= 0 || height <= 0) {
            throw new ParcelException("Box dimensions must all be greater than 0!");
        }
        if (length > MAX_DIMENSION || width > MAX_DIMENSION || height > MAX_DIMENSION) {
            throw new ParcelException(String.format("Box dimensions cannot exceed %.0f cm!", MAX_DIMENSION));
        }
        if (weight > MAX_WEIGHT) {
            throw new ParcelException(String.format("Box weight cannot exceed %.1f kg! Entered: %.2f kg", MAX_WEIGHT, weight));
        }

        this.length = length;
        this.width = width;
        this.height = height;

        this.deliveryService.validate(getChargeableWeight());
    }

    public double getLength() { return length; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public double getVolumetricWeight() {
        return (length * width * height) / VOLUMETRIC_DIVISOR;
    }

    @Override public String getType() { return "Box"; }

    @Override
    public double getChargeableWeight() {
        double maxW = Math.max(weight, getVolumetricWeight());
        double sanitized = Math.round(maxW * 1_000_000.0) / 1_000_000.0;
        return Math.ceil(sanitized);
    }

    @Override
    public double calculateBaseFee() {
        return RATE_PER_KG * getChargeableWeight();
    }
}
