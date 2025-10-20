import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;


class ParkingRecord {
    String plate, type, slot;
    LocalDate date;
    LocalTime in, out;
    long hours;
    double fee;

    // Initializes a new parking record
    ParkingRecord(String plate, String type, String slot, LocalDate date, LocalTime in) {
        this.plate = plate;
        this.type = type;
        this.slot = slot;
        this.date = date;
        this.in = in;
    }

    // Set the time-out, calculate the hours and fee
    void setOut(LocalTime out) {
        this.out = out;
        // Calculate full hours between in and out
        hours = Duration.between(in, out).toHours();
        // If there's a remainder (not a full hour), round up
        if (Duration.between(in, out).toMinutes() % 60 != 0) hours++;
        // Set fee based on vehicle type
        fee = (type.equalsIgnoreCase("Car") || type.equalsIgnoreCase("Van")) ? hours * 20 : hours * 10;
    }
}

// Main Parking System class
public class ParkingSystem {
    static Scanner sc = new Scanner(System.in);         // Scanner for user input
    static ArrayList<ParkingRecord> list = new ArrayList<>(); // List to hold parking records
    static DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy"); // Date format
    static DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");    // Time format (e.g., 03:15 PM)

    // Main method: Program entry point
    public static void main(String[] args) {
        int c;
        do {
            menu();             // Display menu
            c = getInt();       // Get user choice
            switch (c) {
                case 1 -> view();   // View completed (removed) parking records
                case 2 -> park();   // Park a new vehicle
                case 3 -> remove(); // Remove a vehicle (set out time and calculate fee)
                case 4 -> report(); // View report of all completed records
                case 5 -> System.out.println("Thank you!"); // Exit message
                default -> System.out.println("Invalid.");   // Invalid input
            }
        } while (c != 5); // Exit loop when user chooses 5
    }

    // Display the main menu options
    static void menu() {
        System.out.println("\n--- PARKING SYSTEM ---");
        System.out.println("1. View");
        System.out.println("2. Park");
        System.out.println("3. Remove");
        System.out.println("4. Report");
        System.out.println("5. Exit");
        System.out.print("Choice: ");
    }

    // View only completed parking records (those with time-out set)
    static void view() {
        List<ParkingRecord> completed = new ArrayList<>();
        for (ParkingRecord r : list)
            if (r.out != null) completed.add(r); // Only include vehicles that have been removed

        if (completed.isEmpty()) {
            System.out.println("No removed vehicles to display.");
            return;
        }

        // Print table header
        System.out.printf("%-4s %-10s %-8s %-8s %-10s %-12s %-8s\n",
                "#", "Date", "In", "Out", "Plate", "Type", "Fee");

        int n = 1;
        for (ParkingRecord r : completed)
            System.out.printf("%-4d %-10s %-8s %-8s %-10s %-12s %-8.2f\n",
                    n++, r.date.format(dateFmt),
                    r.in.format(timeFmt),
                    r.out.format(timeFmt),
                    r.plate, r.type, r.fee);
    }

    // Park a new vehicle by recording details
    static void park() {
        try {
            // Get user input
            System.out.print("Plate: "); String p = sc.nextLine();
            System.out.print("Type (Car/Van/Motorcycle): "); String t = sc.nextLine();
            System.out.print("Slot: "); String s = sc.nextLine();
            System.out.print("Date (MM/dd/yyyy): "); LocalDate d = LocalDate.parse(sc.nextLine(), dateFmt);
            System.out.print("Time In (hh:mm a): "); LocalTime i = LocalTime.parse(sc.nextLine(), timeFmt);

            // Add new parking record to the list
            list.add(new ParkingRecord(p, t, s, d, i));
            System.out.println("Parked.");
        } catch (Exception e) {
            System.out.println("Invalid input."); // Handle invalid format
        }
    }

    // Remove a vehicle (set its time out and calculate fees)
    static void remove() {
        if (list.isEmpty()) {
            System.out.println("No vehicles.");
            return;
        }

        System.out.print("Plate to remove: ");
        String p = sc.nextLine();

        ParkingRecord f = null;
        // Search for the parking record by plate
        for (ParkingRecord r : list)
            if (r.plate.equalsIgnoreCase(p)) { f = r; break; }

        if (f == null) {
            System.out.println("Not found.");
            return;
        }

        try {
            // Read and set time-out
            System.out.print("Time Out (hh:mm a): ");
            LocalTime o = LocalTime.parse(sc.nextLine(), timeFmt);
            f.setOut(o); // Calculate duration and fee

            // Display confirmation and details
            System.out.println("\nRemoved: " + f.plate + " (" + f.type + ")");
            System.out.println("In: " + f.in + "  Out: " + f.out);
            System.out.println("Hours: " + f.hours + "  Fee: PHP " + f.fee);
        } catch (Exception e) {
            System.out.println("Invalid time."); // Handle time format error
        }
    }

    // Generate a report of all completed records
    static void report() {
        if (list.isEmpty()) {
            System.out.println("No data.");
            return;
        }

        List<ParkingRecord> completed = new ArrayList<>();
        for (ParkingRecord r : list)
            if (r.out != null) completed.add(r); // Only include records with time-out set

        if (completed.isEmpty()) {
            System.out.println("\nNo completed parking records to report.");
            return;
        }

        double totalFees = 0;
        int totalVehicles = 0;

        // Print report header
        System.out.println("\n--- PARKING REPORT ---");
        System.out.printf("%-3s %-12s %-10s %-12s %-12s %-7s %-10s%n",
                "#", "Date", "Time-in", "Plate", "Type", "Hours", "Fee");

        int count = 1;
        for (ParkingRecord r : completed) {
            // Print each completed record
            System.out.printf("%-3d %-12s %-10s %-12s %-12s %-7d %-10.2f%n",
                    count++, r.date.format(dateFmt), r.in.format(timeFmt),
                    r.plate, r.type, r.hours, r.fee);
            totalVehicles++;
            totalFees += r.fee;
        }

        // Print totals
        System.out.println("-----------------------------------------------------------");
        System.out.println("Total Vehicles: " + totalVehicles);
        System.out.printf("Total Fees Collected: ₱%.2f%n", totalFees);
    }

    // Utility method to safely get an integer from user input
    static int getInt() {
        try { 
            return Integer.parseInt(sc.nextLine()); // Try to parse input as integer
        }
        catch (Exception e) { 
            return -1; // Return -1 if input is invalid
        }
    }
}
