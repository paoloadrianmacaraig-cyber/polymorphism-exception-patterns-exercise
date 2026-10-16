import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static final Scanner in = new Scanner(System.in);

    private static final ParcelManager manager = ParcelManager.getInstance();

    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("       PARCEL DELIVERY SYSTEM        ");
        System.out.println("=====================================");

        try {
            while (true) {
                System.out.println("\n--- MAIN MENU ---");
                System.out.println("1. Add Parcel");
                System.out.println("2. View All Parcels");
                System.out.println("3. Change Delivery Service");
                System.out.println("4. Mark as Delivered");
                System.out.println("5. Exit");

                switch (ask("Choose")) {
                    case "1": addParcelFlow(); break;
                    case "2": viewAllParcels(); break;
                    case "3":
                        if (manager.getAllParcels().isEmpty()) {
                            System.out.println("No parcels currently in the system.");
                            break;
                        }
                        changeDeliveryServiceFlow();
                        break;
                    case "4":
                        if (manager.getAllParcels().isEmpty()) {
                            System.out.println("No parcels currently in the system.");
                            break;
                        }
                        markAsDeliveredFlow();
                        break;
                    case "5": System.out.println("Goodbye!"); return;
                    default: System.out.println("Invalid choice.");
                }
            }
        } catch (NoSuchElementException eof) {
        }
    }

    private static void addParcelFlow() {
        System.out.println("\n--- ADD PARCEL ---");
        System.out.println("1. Document\n2. Box\n3. Fragile");
        String type = ask("Select Type");
        if (!type.equals("1") && !type.equals("2") && !type.equals("3")) {
            System.out.println("Invalid parcel type.");
            return;
        }

        String trackingNumber;
        while (true) {
            trackingNumber = ask("Tracking Number (format: 3 letters followed by 4 digits, e.g. ABC0001)").toUpperCase();
            if (!Parcel.TRACKING_PATTERN.matcher(trackingNumber).matches()) {
                System.out.println("Invalid Tracking Number! Must be 3 letters followed by 4 digits (e.g. ABC0001).");
                continue;
            }
            if (manager.isTaken(trackingNumber)) {
                System.out.println("Tracking number " + trackingNumber + " already exists!");
                continue;
            }
            break;
        }

        String receiver;
        while (true) {
            receiver = ask("Receiver Name");
            if (receiver.isEmpty()) {
                System.out.println("Receiver name cannot be empty!");
                continue;
            }
            if (receiver.matches(".*\\d.*") && receiver.matches(".*[^a-zA-Z0-9\\s].*")) {
                System.out.println("Receiver name cannot contain numbers or special characters!");
                continue;
            }
            if (receiver.matches(".*\\d.*")) {
                System.out.println("Receiver name cannot contain numbers!");
                continue;
            }
            if (receiver.matches(".*[^a-zA-Z\\s].*")) {
                System.out.println("Receiver name cannot contain special characters!");
                continue;
            }
            break;
        }

        double maxWeight = type.equals("1") ? Document.MAX_WEIGHT : type.equals("2") ? Box.MAX_WEIGHT : Fragile.FRAGILE_MAX_WEIGHT;
        String typeName = type.equals("1") ? "Document" : type.equals("2") ? "Box" : "Fragile";
        String maxWeightStr = (maxWeight == (long) maxWeight) ? String.valueOf((long) maxWeight) : String.valueOf(maxWeight);
        String weightPrompt = String.format("Weight in kg (0-%skg)", maxWeightStr);

        double weight;
        while (true) {
            String rawWeight = ask(weightPrompt);
            try {
                weight = Double.parseDouble(rawWeight);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid decimal number.");
                continue;
            }
            if (weight <= 0) {
                System.out.println("Weight must be greater than 0 kg!");
                continue;
            }
            if (weight > maxWeight) {
                System.out.printf("%s weight cannot exceed %s kg! Entered: %.2f kg%n", typeName, maxWeightStr, weight);
                continue;
            }
            break;
        }

        int pages = 0;
        double length = 0;
        double width = 0;
        double height = 0;
        String note = "";
        double chargeableWeight;

        if (type.equals("1")) {
            while (true) {
                String rawPages = ask("Number of Pages (1 to 100)");
                int p;
                try {
                    p = Integer.parseInt(rawPages);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a whole number from 1 to 100.");
                    continue;
                }
                if (p < 1 || p > 100) {
                    System.out.println("Number of pages must be between 1 and 100!");
                    continue;
                }
                pages = p;
                break;
            }
            chargeableWeight = weight;
        } else {
            while (true) {
                String raw = ask(String.format("Length in cm (0-%.0fcm)", Box.MAX_DIMENSION));
                try {
                    length = Double.parseDouble(raw);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a valid decimal number.");
                    continue;
                }
                if (length <= 0) { System.out.println("Length must be greater than 0 cm!"); continue; }
                if (length > Box.MAX_DIMENSION) {
                    System.out.printf("Length cannot exceed %.0f cm! Entered: %.2f cm%n", Box.MAX_DIMENSION, length);
                    continue;
                }
                break;
            }
            while (true) {
                String raw = ask(String.format("Width in cm (0-%.0fcm)", Box.MAX_DIMENSION));
                try {
                    width = Double.parseDouble(raw);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a valid decimal number.");
                    continue;
                }
                if (width <= 0) { System.out.println("Width must be greater than 0 cm!"); continue; }
                if (width > Box.MAX_DIMENSION) {
                    System.out.printf("Width cannot exceed %.0f cm! Entered: %.2f cm%n", Box.MAX_DIMENSION, width);
                    continue;
                }
                break;
            }
            while (true) {
                String raw = ask(String.format("Height in cm (0-%.0fcm)", Box.MAX_DIMENSION));
                try {
                    height = Double.parseDouble(raw);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a valid decimal number.");
                    continue;
                }
                if (height <= 0) { System.out.println("Height must be greater than 0 cm!"); continue; }
                if (height > Box.MAX_DIMENSION) {
                    System.out.printf("Height cannot exceed %.0f cm! Entered: %.2f cm%n", Box.MAX_DIMENSION, height);
                    continue;
                }
                break;
            }

            double volumetric = (length * width * height) / Box.VOLUMETRIC_DIVISOR;
            double maxW = Math.max(weight, volumetric);
            double sanitized = Math.round(maxW * 1_000_000.0) / 1_000_000.0;
            chargeableWeight = Math.ceil(sanitized);

            if (type.equals("3")) {
                while (true) {
                    note = ask("Handling Note");
                    if (note.isEmpty()) {
                        System.out.println("Handling note cannot be empty!");
                        continue;
                    }
                    break;
                }
            }
        }

        DeliveryStrategy service = selectDeliveryService(chargeableWeight);

        try {
            Parcel parcel;
            if (type.equals("1")) {
                parcel = new Document(trackingNumber, receiver, weight, service, pages);
            } else if (type.equals("2")) {
                parcel = new Box(trackingNumber, receiver, weight, service, length, width, height);
            } else {
                parcel = new Fragile(trackingNumber, receiver, weight, service, length, width, height, note);
            }

            manager.addParcel(parcel);

            System.out.printf("Parcel added successfully! Delivery fee: Php %.2f%n", parcel.calculateDeliveryFee());
        } catch (ParcelException e) {
            System.out.println("! " + e.getMessage());
        }
    }

    private static DeliveryStrategy selectDeliveryService(double chargeableWeight) {
        while (true) {
            System.out.println("Delivery Service: 1. Standard  2. Express  3. Same Day");
            String choice = ask("Choose Service");
            DeliveryStrategy s = parseService(choice);
            if (s == null) {
                System.out.println("Invalid selection. Choose 1, 2, or 3.");
                continue;
            }
            try {
                s.validate(chargeableWeight);
                return s;
            } catch (ParcelException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static DeliveryStrategy parseService(String choice) {
        switch (choice) {
            case "1": return new StandardDelivery();
            case "2": return new ExpressDelivery();
            case "3": return new SameDayDelivery();
            default: return null;
        }
    }

    private static void viewAllParcels() {
        if (manager.getAllParcels().isEmpty()) {
            System.out.println("No parcels currently in the system.");
            return;
        }

        List<Parcel> pending = manager.getPendingParcels();
        List<Parcel> delivered = manager.getDeliveredParcels();

        System.out.println("\n--- PENDING PARCELS ---");
        if (pending.isEmpty()) {
            System.out.println("No pending parcels.");
        } else {
            printParcelTable(pending);
            System.out.println("\nTotal pending parcels: " + pending.size());
        }

        System.out.println("\n--- DELIVERED PARCELS ---");
        if (delivered.isEmpty()) {
            System.out.println("No delivered parcels yet.");
        } else {
            printParcelTable(delivered);
            System.out.println("\nTotal delivered parcels: " + delivered.size());
        }
    }

    private static void printParcelTable(List<Parcel> list) {
        String format = "%-12s %-10s %-20s %-15s %-10s %-15s %s%n";
        System.out.printf(format, "Tracking No.", "Type", "Receiver", "Chargeable Wt", "Service", "Delivery Fee", "Status");
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (Parcel p : list) {
            System.out.printf(format,
                    p.getTrackingNumber(),
                    p.getType(),
                    truncate(p.getReceiverName(), 20),
                    p.getFormattedChargeableWeight(),
                    p.getDeliveryService().getName(),
                    String.format("Php %.2f", p.calculateDeliveryFee()),
                    p.getStatus());
        }
    }

    private static void changeDeliveryServiceFlow() {
        if (manager.getAllParcels().isEmpty()) {
            System.out.println("No parcels currently in the system.");
            return;
        }

        System.out.println("\n--- CHANGE DELIVERY SERVICE ---");
        String tn = ask("Enter Tracking Number").toUpperCase();

        Parcel parcel;
        try {
            parcel = manager.getParcel(tn);
        } catch (ParcelException e) {
            System.out.println("Parcel not found!");
            return;
        }

        if (parcel.isDelivered()) {
            System.out.println("Parcel already delivered!");
            return;
        }

        System.out.printf("Current Service: %s | Fee: Php %.2f | Chargeable: %s%n",
                parcel.getDeliveryService().getName(),
                parcel.calculateDeliveryFee(),
                parcel.getFormattedChargeableWeight());

        System.out.println("1. Standard  2. Express  3. Same Day");
        String choice = ask("New Service");
        DeliveryStrategy newService = parseService(choice);
        if (newService == null) {
            System.out.println("Invalid selection.");
            return;
        }

        try {
            double newFee = manager.changeDeliveryService(tn, newService);
            System.out.printf("Delivery service changed! New fee: Php %.2f%n", newFee);
        } catch (ParcelException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void markAsDeliveredFlow() {
        if (manager.getAllParcels().isEmpty()) {
            System.out.println("No parcels currently in the system.");
            return;
        }

        System.out.println("\n--- MARK AS DELIVERED ---");
        String tn = ask("Enter Tracking Number").toUpperCase();

        try {
            manager.markAsDelivered(tn);
            System.out.println("Parcel delivered successfully!");
        } catch (ParcelException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String ask(String prompt) {
        System.out.print(prompt + ": ");
        return in.hasNextLine() ? in.nextLine().trim() : "";
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return -1; }
    }

    private static double parseDouble(String s) {
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return -1.0; }
    }

    private static String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
