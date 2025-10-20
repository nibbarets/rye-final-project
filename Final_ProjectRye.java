import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

class ParkingRecord {
    String plate, type, slot;
    LocalDate date;
    LocalTime in, out;
    long hours;
    double fee;

    ParkingRecord(String plate, String type, String slot, LocalDate date, LocalTime in) {
        this.plate = plate;
        this.type = type;
        this.slot = slot;
        this.date = date;
        this.in = in;
    }

    void setOut(LocalTime out) {
        this.out = out;
        hours = Duration.between(in, out).toHours();
        if (Duration.between(in, out).toMinutes() % 60 != 0) hours++;
        fee = (type.equalsIgnoreCase("Car") || type.equalsIgnoreCase("Van")) ? hours * 20 : hours * 10;
    }
}

public class ParkingSystem {
    static Scanner sc = new Scanner(System.in);
    static ArrayList<ParkingRecord> list = new ArrayList<>();
    static DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    static DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");

    public static void main(String[] args) {
        int c;
        do {
            menu();
            c = getInt();
            switch (c) {
                case 1 -> view();
                case 2 -> park();
                case 3 -> remove();
                case 4 -> report();
                case 5 -> System.out.println("Thank you!");
                default -> System.out.println("Invalid.");
            }
        } while (c != 5);
    }

    static void menu() {
        System.out.println("\n--- PARKING SYSTEM ---");
        System.out.println("1. View");
        System.out.println("2. Park");
        System.out.println("3. Remove");
        System.out.println("4. Report");
        System.out.println("5. Exit");
        System.out.print("Choice: ");
    }

    // Modified: Only display vehicles that have been removed (with timeOut)
    static void view() {
        List<ParkingRecord> completed = new ArrayList<>();
        for (ParkingRecord r : list)
            if (r.out != null) completed.add(r);

        if (completed.isEmpty()) {
            System.out.println("No removed vehicles to display.");
            return;
        }

        System.out.printf("%-4s %-10s %-8s %-10s %-12s %-8s %-8s\n",
                "#", "Date", "In", "Out", "Plate", "Type", "Fee");

        int n = 1;
        for (ParkingRecord r : completed)
            System.out.printf("%-4d %-10s %-8s %-8s %-10s %-12s %-8.2f\n",
                    n++, r.date.format(dateFmt),
                    r.in.format(timeFmt),
                    r.out.format(timeFmt),
                    r.plate, r.type, r.fee);
    }

    static void park() {
        try {
            System.out.print("Plate: "); String p = sc.nextLine();
            System.out.print("Type (Car/Van/Motorcycle): "); String t = sc.nextLine();
            System.out.print("Slot: "); String s = sc.nextLine();
            System.out.print("Date (MM/dd/yyyy): "); LocalDate d = LocalDate.parse(sc.nextLine(), dateFmt);
            System.out.print("Time In (hh:mm a): "); LocalTime i = LocalTime.parse(sc.nextLine(), timeFmt);
            list.add(new ParkingRecord(p, t, s, d, i));
            System.out.println("Parked.");
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    static void remove() {
        if (list.isEmpty()) {
            System.out.println("No vehicles.");
            return;
        }
        System.out.print("Plate to remove: ");
        String p = sc.nextLine();
        ParkingRecord f = null;
        for (ParkingRecord r : list)
            if (r.plate.equalsIgnoreCase(p)) { f = r; break; }
        if (f == null) { System.out.println("Not found."); return; }
        try {
            System.out.print("Time Out (hh:mm a): ");
            LocalTime o = LocalTime.parse(sc.nextLine(), timeFmt);
            f.setOut(o);
            System.out.println("\nRemoved: " + f.plate + " (" + f.type + ")");
            System.out.println("In: " + f.in + "  Out: " + f.out);
            System.out.println("Hours: " + f.hours + "  Fee: PHP " + f.fee);
        } catch (Exception e) {
            System.out.println("Invalid time.");
        }
    }

    static void report() {
        if (list.isEmpty()) {
            System.out.println("No data.");
            return;
        }

        List<ParkingRecord> completed = new ArrayList<>();
        for (ParkingRecord r : list)
            if (r.out != null) completed.add(r);

        if (completed.isEmpty()) {
            System.out.println("\nNo completed parking records to report.");
            return;
        }

        double totalFees = 0;
        int totalVehicles = 0;

        System.out.println("\n--- PARKING REPORT ---");
        System.out.printf("%-3s %-12s %-10s %-12s %-12s %-7s %-10s%n",
                "#", "Date", "Time-in", "Plate", "Type", "Hours", "Fee");

        int count = 1;
        for (ParkingRecord r : completed) {
            System.out.printf("%-3d %-12s %-10s %-12s %-12s %-7d %-10.2f%n",
                    count++, r.date.format(dateFmt), r.in.format(timeFmt),
                    r.plate, r.type, r.hours, r.fee);
            totalVehicles++;
            totalFees += r.fee;
        }

        System.out.println("-----------------------------------------------------------");
        System.out.println("Total Vehicles: " + totalVehicles);
        System.out.printf("Total Fees Collected: ₱%.2f%n", totalFees);
    }

    static int getInt() {
        try { return Integer.parseInt(sc.nextLine()); }
        catch (Exception e) { return -1; }
    }
}
