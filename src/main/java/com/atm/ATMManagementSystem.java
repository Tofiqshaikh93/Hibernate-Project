package com.atm;

import com.atm.entity.*;


import com.atm.exception.ATMException;
import com.atm.service.ATMService;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ATMManagementSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ATMService atmService = new ATMService();

    public static void main(String[] args) {
        try {
            if (testDatabaseConnection()) {
                atmService.initializeSystem();
                runMainMenu();
            } else {
                System.out.println("Failed to connect to the database. Please check your configuration.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred while initializing the system: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static boolean testDatabaseConnection() {
        String url = "jdbc:mysql://localhost:3306/MYATM?createDatabaseIfNotExist=true";
        String user = "root";
        String password = "Tofiq@123";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Database connection successful!");
            return true;
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
            return false;
        }
    }

    private static void runMainMenu() {
        while (true) {
            System.out.println("\n===== WELCOME TO BARISTA ATM =====");
            System.out.println("1. Admin");
            System.out.println("2. Customer");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    adminLogin();
                    break;
                case 2:
                    customerLogin();
                    break;
                case 3:
                    System.out.println("Thank you for using BARISTA ATM. Have a great day!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void adminLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = atmService.authenticateUser(username, password);
        if (user != null && user.isAdmin()) {
            System.out.println("Welcome, " + user.getName() + "! You've successfully logged in as an admin.");
            adminMenu();
        } else {
            System.out.println("Invalid admin credentials. Please try again.");
        }
    }

//    private static void customerLogin() {
//        System.out.print("Enter account number: ");
//        String accountNumber = scanner.nextLine();
//        System.out.print("Enter ATM PIN: ");
//        String pin = scanner.nextLine();
//
//        try {
//            System.out.println("Debug: Searching for account number: " + accountNumber);
//            Account account = atmService.findAccountByAccountNumber(accountNumber);
//            if (account != null) {
//                System.out.println("Debug: Account found.");
//                System.out.println("Debug: Account details:");
//                System.out.println("Debug: Name: " + account.getUser().getName());
//                System.out.println("Debug: Account Number: " + account.getAccountNumber());
//                System.out.println("Debug: Stored PIN: " + account.getPin());
//                System.out.println("Debug: Entered PIN: " + pin);
//                if (account.getPin() != null && account.getPin().equals(pin)) {
//                    System.out.println("Welcome, " + account.getUser().getName() + "! You've successfully logged in.");
//                    customerMenu(account.getId());
//                } else {
//                    System.out.println("Invalid account number or PIN. Please try again.");
//                }
//            } else {
//                System.out.println("Debug: Account not found for account number: " + accountNumber);
//                System.out.println("Invalid account number or PIN. Please try again.");
//            }
//        } catch (ATMException e) {
//            System.out.println("Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
    
    private static void customerLogin() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter ATM PIN: ");
        String pin = scanner.nextLine();

        try {
            Account account = atmService.findAccountByAccountNumber(accountNumber);
            if (account != null) {
                System.out.println("Debug: Account found. Stored PIN: " + account.getPin());
                if (account.getPin().equals(pin)) {
                    System.out.println("Welcome, " + account.getUser().getName() + "! You've successfully logged in.");
                    customerMenu(account.getId());
                } else {
                    System.out.println("Invalid account number or PIN. Please try again.");
                }
            } else {
                System.out.println("Invalid account number or PIN. Please try again.");
            }
        } catch (ATMException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. Add Customer");
            System.out.println("2. Manage Customer Accounts");
            System.out.println("3. View Audit Logs");
            System.out.println("4. Generate Reports");
            System.out.println("5. Monitor ATM Cash");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addCustomer();
                    break;
                case 2:
                    manageCustomerAccounts();
                    break;
                case 3:
                    viewAuditLogs();
                    break;
                case 4:
                    generateReports();
                    break;
                case 5:
                    monitorATMCash();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addCustomer() {
        System.out.println("\n----- Add New Customer -----");
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter initial balance to deposit: ");
        BigDecimal initialBalance = scanner.nextBigDecimal();
        scanner.nextLine(); // Consume newline

        String pin = atmService.generateATMPin();
        
        try {
            atmService.addCustomer(name, accountNumber, pin, initialBalance);
            System.out.println("Customer added successfully!");
            System.out.println("Generated ATM PIN: " + pin);
            System.out.println("Please provide this PIN to the customer securely.");
        } catch (ATMException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void manageCustomerAccounts() {
        System.out.println("\n----- Manage Customer Accounts -----");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        try {
            Account account = atmService.findAccountByAccountNumber(accountNumber);
            if (account != null) {
                System.out.println("Account found for: " + account.getUser().getName());
                System.out.println("1. Update Account Details");
                System.out.println("2. Delete Account");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        updateAccountDetails(account);
                        break;
                    case 2:
                        deleteAccount(account);
                        break;
                    default:
                        System.out.println("Invalid option. Returning to Admin Menu.");
                }
            } else {
                System.out.println("Account not found. Please try again.");
            }
        } catch (ATMException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateAccountDetails(Account account) {
        System.out.println("\n----- Update Account Details -----");
        System.out.print("Enter new name (or press Enter to keep current): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            account.getUser().setName(newName);
        }

        System.out.print("Enter new PIN (or press Enter to keep current): ");
        String newPin = scanner.nextLine();
        if (!newPin.isEmpty()) {
            account.setPin(newPin);
        }

        atmService.updateAccount(account);
        System.out.println("Account details updated successfully!");
    }

    private static void deleteAccount(Account account) {
        System.out.println("\n----- Delete Account -----");
        System.out.print("Are you sure you want to delete this account? (yes/no): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            atmService.deleteAccount(account);
            System.out.println("Account deleted successfully!");
        } else {
            System.out.println("Account deletion cancelled.");
        }
    }

    private static void viewAuditLogs() {
        System.out.println("\n----- View Audit Logs -----");
        List<com.atm.entity.Transaction> transactions = atmService.getAllTransactions();
        for (com.atm.entity.Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    private static void generateReports() {
        System.out.println("\n----- Generate Reports -----");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        Account account = atmService.findAccountByAccountNumber(accountNumber);
        if (account != null) {
            List<com.atm.entity.Transaction> transactions = atmService.getTransactionsByAccount(account);
            System.out.println("Transaction history for " + account.getUser().getName() + " (Account: " + accountNumber + "):");
            for (com.atm.entity.Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        } else {
            System.out.println("Account not found. Please try again.");
        }
    }

    private static void monitorATMCash() {
        System.out.println("\n----- Monitor ATM Cash -----");
        ATM atm = atmService.getATMByName("BARISTA");
        System.out.println("Current ATM balance: " + atm.getCashAvailable());
        
        System.out.println("1. Add Cash");
        System.out.println("2. Back to Admin Menu");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            System.out.print("Enter amount to add: ");
            BigDecimal amount = scanner.nextBigDecimal();
            scanner.nextLine(); // Consume newline
            atmService.addCashToATM(atm, amount);
            System.out.println("Cash added successfully. New ATM balance: " + atm.getCashAvailable());
        }
    }

    private static void customerMenu(Long accountId) {
        try {
            Account account = atmService.getAccountById(accountId);
            while (true) {
                System.out.println("\n===== CUSTOMER MENU =====");
                System.out.println("Welcome, " + account.getUser().getName() + "!");
                System.out.println("1. View Balance");
                System.out.println("2. Withdraw Amount");
                System.out.println("3. Deposit");
                System.out.println("4. Generate Mini Statement");
                System.out.println("5. Back to Main Menu");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                try {
                    switch (choice) {
                        case 1:
                            viewBalance(account);
                            break;
                        case 2:
                            account = withdrawAmount(account);
                            break;
                        case 3:
                            account = deposit(account);
                            break;
                        case 4:
                            generateMiniStatement(account);
                            break;
                        case 5:
                            System.out.println("Thank you for using BARISTA ATM, " + account.getUser().getName() + ". Have a great day!");
                            return;
                        default:
                            System.out.println("Invalid option. Please try again.");
                    }
                    // Refresh the account after each operation
                    account = atmService.getAccountById(account.getId());
                } catch (ATMException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (ATMException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Returning to main menu.");
        }
    }

    private static void viewBalance(Account account) {
        System.out.println("\n----- View Balance -----");
        System.out.println("Processing...");
        try {
            Thread.sleep(1000); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Your current balance is: ₹" + account.getBalance());
    }

    private static Account withdrawAmount(Account account) {
        System.out.println("\n----- Withdraw Amount -----");
        System.out.println("Please note: Withdrawal amount should be in multiples of 100.");
        System.out.print("Enter amount to withdraw: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine(); // Consume newline

        if (amount.remainder(new BigDecimal("100")).compareTo(BigDecimal.ZERO) != 0) {
            System.out.println("Invalid amount. Please enter an amount in multiples of 100.");
            return account;
        }

        System.out.println("Processing withdrawal...");
        try {
            Thread.sleep(2000); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            account = atmService.withdraw(account.getId(), amount);
            System.out.println("Withdrawal successful. Amount withdrawn: ₹" + amount);
            System.out.println("Your updated balance is: ₹" + account.getBalance());
            System.out.println("Thank you for using BARISTA ATM.");
        } catch (ATMException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
        return account;
    }

    private static Account deposit(Account account) {
        System.out.println("\n----- Deposit -----");
        System.out.print("Enter amount to deposit: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine(); // Consume newline

        System.out.println("Processing deposit...");
        try {
            Thread.sleep(2000); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            account = atmService.deposit(account.getId(), amount);
            System.out.println("₹" + amount + " successfully deposited to your account.");
            System.out.println("Your updated balance is: ₹" + account.getBalance());
            System.out.println("Thank you for your transaction with BARISTA ATM.");
        } catch (ATMException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
        return account;
    }

    private static void generateMiniStatement(Account account) {
        System.out.println("\n----- Mini Statement -----");
        System.out.println("Generating mini statement...");
        try {
            Thread.sleep(1500); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<com.atm.entity.Transaction> transactions = atmService.getRecentTransactions(account, 10);
        System.out.println("Recent Transactions for Account: " + account.getAccountNumber());
        System.out.println("--------------------------------------------");
        for (int i = 0; i < transactions.size(); i++) {
            com.atm.entity.Transaction t = transactions.get(i);
            System.out.printf("%d. %s - %s: ₹%.2f%n", 
                i + 1, 
                t.getTimestamp().toString(), 
                t.getType(), 
                t.getAmount());
        }
        System.out.println("--------------------------------------------");
        System.out.println("End of mini statement. Thank you for using BARISTA ATM.");
    }
}

