import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Transaction {
    private String type;       // income or expense
    private String category;   // e.g., Food, Salary
    private double amount;
    private LocalDate date;
    private String description;

    public Transaction(String type, String category, double amount, LocalDate date, String description) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public String getType() { return type; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return date + " | " + type.toUpperCase() + " | " + category + " | $" + amount + " | " + description;
    }

    public String toCSV() {
        return date + "," + type + "," + category + "," + amount + "," + description;
    }
}

public class ExpenseTracker {
    private static final String FILE_NAME = "transactions.csv";
    private static List<Transaction> transactions = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadTransactions(); // Load data from file on startup
        while (true) {
            System.out.println("\n==== Expense Tracker ====");
            System.out.println("1. Add Transaction");
            System.out.println("2. View Transactions");
            System.out.println("3. View Monthly Summary");
            System.out.println("4. Export to CSV");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> addTransaction();
                case 2 -> viewTransactions();
                case 3 -> viewMonthlySummary();
                case 4 -> exportTransactions();
                case 5 -> {
                    System.out.println("Goodbye! Your data is saved.");
                    saveTransactions();
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Add a new transaction
    private static void addTransaction() {
        try {
            System.out.print("Enter type (income/expense): ");
            String type = scanner.nextLine().trim().toLowerCase();

            System.out.print("Enter category: ");
            String category = scanner.nextLine().trim();

            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter date (yyyy-mm-dd): ");
            LocalDate date = LocalDate.parse(scanner.nextLine().trim());

            System.out.print("Enter description: ");
            String description = scanner.nextLine().trim();

            transactions.add(new Transaction(type, category, amount, date, description));
            System.out.println("✅ Transaction added successfully!");
        } catch (Exception e) {
            System.out.println("❌ Error: Please enter valid details.");
        }
    }

    // Display all transactions
    private static void viewTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.println("\nDate | Type | Category | Amount | Description");
        System.out.println("--------------------------------------------------");
        transactions.forEach(System.out::println);
    }

    // View monthly summary
    private static void viewMonthlySummary() {
        System.out.print("Enter month and year (MM-yyyy): ");
        String[] input = scanner.nextLine().split("-");
        int month = Integer.parseInt(input[0]);
        int year = Integer.parseInt(input[1]);

        double income = 0, expense = 0;
        for (Transaction t : transactions) {
            if (t.getDate().getMonthValue() == month && t.getDate().getYear() == year) {
                if (t.getType().equalsIgnoreCase("income")) {
                    income += t.getAmount();
                } else {
                    expense += t.getAmount();
                }
            }
        }

        System.out.println("\n📅 Summary for " + month + "-" + year);
        System.out.println("Total Income: $" + income);
        System.out.println("Total Expense: $" + expense);
        System.out.println("Net Balance: $" + (income - expense));
    }

    // Save transactions to CSV file
    private static void saveTransactions() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Transaction t : transactions) {
                writer.write(t.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Error saving transactions.");
        }
    }

    // Load transactions from CSV file
    private static void loadTransactions() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                Transaction t = new Transaction(
                        data[1], data[2],
                        Double.parseDouble(data[3]),
                        LocalDate.parse(data[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        data[4]
                );
                transactions.add(t);
            }
        } catch (IOException e) {
            System.out.println("❌ Error loading transactions.");
        }
    }

    // Export transactions to CSV (same as save, but separate menu option)
    private static void exportTransactions() {
        saveTransactions();
        System.out.println("✅ Transactions exported to " + FILE_NAME);
    }
}
