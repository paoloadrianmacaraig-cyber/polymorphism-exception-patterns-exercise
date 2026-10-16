public interface DeliveryStrategy {
    String getName();
    double calculateFee(double baseFee);
    void validate(double chargeableWeight) throws ParcelException;
}

class StandardDelivery implements DeliveryStrategy {
    @Override public String getName() { return "Standard"; }
    @Override public double calculateFee(double baseFee) { return baseFee; }
    @Override public void validate(double chargeableWeight) throws ParcelException {}
}

class ExpressDelivery implements DeliveryStrategy {
    @Override public String getName() { return "Express"; }
    @Override public double calculateFee(double baseFee) { return baseFee * 1.5; }
    @Override public void validate(double chargeableWeight) throws ParcelException {}
}

class SameDayDelivery implements DeliveryStrategy {
    @Override public String getName() { return "Same Day"; }
    @Override public double calculateFee(double baseFee) { return (baseFee * 2.0) + 100.0; }

    @Override
    public void validate(double chargeableWeight) throws ParcelException {
        if (chargeableWeight > 10.0) {
            throw new ParcelException("Same Day delivery is only for parcels up to 10 kg!");
        }
    }
}
