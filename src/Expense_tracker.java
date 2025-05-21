import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.*;
class Transaction {
    String type; // "Income" or "Expense"
    LocalDate date;
    String category;
    double amount;
    String description;

    Transaction(String type, LocalDate date, String category, double amount, String description) {
        this.type = type;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public String toString() {
        return type + "," + date + "," + category + "," + amount + "," + description;
    }

    static Transaction fromString(String line) {
        String[] parts = line.split(",", 5);
        return new Transaction(parts[0], LocalDate.parse(parts[1]), parts[2], Double.parseDouble(parts[3]), parts[4]);
    }
}

class TransactionManager {
    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public void clearTransactions() {
        transactions.clear();
    }

    public void loadFromFile(String filename) {
        transactions.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                transactions.add(Transaction.fromString(line));
            }
            System.out.println("Data loaded from " + filename);
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Transaction t : transactions) {
                writer.println(t);
            }
            System.out.println("Data saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error writing file.");
        }
    }

    public void showMonthlySummary(String month) {
        double totalIncome = 0;
        double totalExpense = 0;
        Map<String, Double> incomeMap = new HashMap<>();
        Map<String, Double> expenseMap = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.date.toString().startsWith(month)) {
                if (t.type.equalsIgnoreCase("Income")) {
                    totalIncome += t.amount;
                    incomeMap.put(t.category, incomeMap.getOrDefault(t.category, 0.0) + t.amount);
                } else {
                    totalExpense += t.amount;
                    expenseMap.put(t.category, expenseMap.getOrDefault(t.category, 0.0) + t.amount);
                }
            }
        }

        System.out.println("\n--- Summary for " + month + " ---");
        System.out.println("Total Income: $" + totalIncome);
        incomeMap.forEach((cat, amt) -> System.out.println("  " + cat + ": $" + amt));
        System.out.println("Total Expense: $" + totalExpense);
        expenseMap.forEach((cat, amt) -> System.out.println("  " + cat + ": $" + amt));
        System.out.println("Net Savings: $" + (totalIncome - totalExpense));
    }
}


public class Expense_tracker {
    private static final Scanner scanner = new Scanner(System.in);
    private static final TransactionManager manager = new TransactionManager();
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n Simple Expense Tracker ");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View Monthly Summary");
            System.out.println("4. Load from File");
            System.out.println("5. Save to File");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> addTransaction("Income");
                case "2" -> addTransaction("Expense");
                case "3" -> viewSummary();
                case "4" -> loadFromFile();
                case "5" -> saveToFile();
                case "6" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private static void addTransaction(String type) {
        try {
            System.out.print("Enter date (yyyy-MM-dd): ");
            LocalDate date = LocalDate.parse(scanner.nextLine(), dateFormat);

            System.out.print("Enter category (" +
                    (type.equals("Income") ? "Salary/Business" : "Food/Rent/Travel") + "): ");
            String category = scanner.nextLine();

            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            Transaction transaction = new Transaction(type, date, category, amount, description);
            manager.addTransaction(transaction);
            System.out.println(type + " added successfully.");
        } catch (Exception e) {
            System.out.println("Invalid input. Please try again.");
        }
    }
    private static void viewSummary() {
        System.out.print("Enter month (yyyy-MM): ");
        String month = scanner.nextLine();
        manager.showMonthlySummary(month);
    }

    private static void loadFromFile() {
        System.out.print("Enter filename to load: ");
        String filename = scanner.nextLine();
        manager.loadFromFile(filename);
    }

    private static void saveToFile() {
        System.out.print("Enter filename to save: ");
        String filename = scanner.nextLine();
        manager.saveToFile(filename);
    }
}

