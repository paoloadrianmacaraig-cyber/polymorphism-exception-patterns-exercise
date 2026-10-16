import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParcelManager {
    private static volatile ParcelManager instance;
    private final Map<String, Parcel> parcels = new LinkedHashMap<>();

    private ParcelManager() {
    }

    public static ParcelManager getInstance() {
        if (instance == null) {
            synchronized (ParcelManager.class) {
                if (instance == null) {
                    instance = new ParcelManager();
                }
            }
        }
        return instance;
    }

    public synchronized void addParcel(Parcel parcel) throws ParcelException {
        if (parcel == null)
            throw new ParcelException("Parcel cannot be null.");
        String key = parcel.getTrackingNumber().toUpperCase();
        if (parcels.containsKey(key)) {
            throw new ParcelException("Tracking number " + parcel.getTrackingNumber() + " already exists!");
        }
        parcels.put(key, parcel);
    }

    public synchronized boolean isTaken(String trackingNumber) {
        if (trackingNumber == null)
            return false;
        return parcels.containsKey(trackingNumber.trim().toUpperCase());
    }

    public synchronized Parcel getParcel(String trackingNumber) throws ParcelException {
        if (trackingNumber == null)
            throw new ParcelException("Parcel not found!");
        Parcel parcel = parcels.get(trackingNumber.trim().toUpperCase());
        if (parcel == null)
            throw new ParcelException("Parcel not found!");
        return parcel;
    }

    public synchronized List<Parcel> getAllParcels() {
        return Collections.unmodifiableList(new ArrayList<>(parcels.values()));
    }

    public synchronized List<Parcel> getPendingParcels() {
        List<Parcel> list = new ArrayList<>();
        for (Parcel p : parcels.values()) {
            if (!p.isDelivered()) list.add(p);
        }
        return Collections.unmodifiableList(list);
    }

    public synchronized List<Parcel> getDeliveredParcels() {
        List<Parcel> list = new ArrayList<>();
        for (Parcel p : parcels.values()) {
            if (p.isDelivered()) list.add(p);
        }
        return Collections.unmodifiableList(list);
    }

    public synchronized double changeDeliveryService(String trackingNumber, DeliveryStrategy newService)
            throws ParcelException {
        Parcel parcel = getParcel(trackingNumber);
        if (parcel.isDelivered()) {
            throw new ParcelException("Parcel already delivered!");
        }
        parcel.changeDeliveryService(newService);
        return parcel.calculateDeliveryFee();
    }

    public synchronized void markAsDelivered(String trackingNumber) throws ParcelException {
        Parcel parcel = getParcel(trackingNumber);
        if (parcel.isDelivered()) {
            throw new ParcelException("Parcel was already delivered.");
        }
        parcel.markDelivered();
    }
}
