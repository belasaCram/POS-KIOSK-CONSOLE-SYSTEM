/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Managers;

import FileSetup.IFoodItemRepository;
import FileSetup.OrderRepository;
import FileSetup.QueueingOrderRepository;
import Objects.FoodItem;
import Objects.Order;
import Objects.QueueingOrder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import poskiosksystem.PosKioskSystem;
/**
 *
 * @author Marc Nelson Belasa
 */
public class KioskManager {
    // Interface for repository that handles food items
    private final IFoodItemRepository itemRepo;

    // Constructor that initializes the item repository
    public KioskManager(IFoodItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    // Main method to start the kiosk system
    public void start() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("\nWelcome to the Kiosk System!");
                System.out.println("\n--- Main Menu ---");
                System.out.println("1. Choose Meal");
                System.out.println("2. View Cart");
                System.out.println("3. Exit");
                System.out.print("\nSelection: ");
                int choice = getIntInput(scan); // Get user input
                scan.nextLine(); // Clear the buffer
                switch (choice) {
                    case 1 -> chooseCategory(); // Option to choose category
                    case 2 -> viewCart(); // Option to view cart
                    case 3 -> {
                        System.out.println("Thank you for using the Kiosk System. Goodbye!");
                        PosKioskSystem main = new PosKioskSystem();
                        main.startSystem();
                    }
                    default -> System.out.println("Invalid selection. Please choose a number between 1 and 3.");
                }
            } catch (InputMismatchException ex) {
                System.err.println("Invalid Input!");
                scan.nextLine(); // Clear the invalid input
            }
        }
    }

    // Method to choose food category
    private void chooseCategory() {
        Scanner scan = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n--- Category Menu ---");
                System.out.println("1. Dessert");
                System.out.println("2. Chicken");
                System.out.println("3. Drinks");
                System.out.println("4. Return to Main Menu");
                System.out.print("\nSelection: ");
                int choice = getIntInput(scan); // Get user input
                scan.nextLine(); // Clear the buffer
                
                if (choice == 4){ 
                    start();
                    return;// Return to main menu
                }else if (choice <= 1 && choice >= 3) {
                    System.out.println("Invalid selection. Please choose a number between 1 and 4.");
                    return;
                }
                
                chooseItem(choice); // Choose item based on category

            } catch (InputMismatchException ex) {
                System.err.println("Invalid Input!");
                scan.nextLine(); // Clear the invalid input
            }
        }
    }

    // Method to choose food item from a selected category
    private void chooseItem(int categoryId) {
        Scanner scan = new Scanner(System.in);
        List<FoodItem> items = itemRepo.getFoodItemsByCategory(categoryId); // Get items by category
        displayItems(items); // Display items

        while (true) {
            System.out.println("\nIf you want to go back type 0.");
            System.out.print("Selection: ");
            int itemId = getIntInput(scan); // Get user input
            scan.nextLine(); // Clear the buffer

            if(itemId == 0){
                chooseCategory();
                return;
            }
            
            Optional<FoodItem> itemOpt = items.stream().filter(item -> item.getId() == itemId).findFirst(); // Find selected item
            if (!itemOpt.isPresent()) {
                System.err.println("Invalid item ID. Please select from the given list.");
                return;
            }
            
            FoodItem item = itemOpt.get();
            System.out.print("Enter the quantity: ");
            int qty = getIntInput(scan); // Get quantity
            scan.nextLine(); // Clear the buffer

            // Create and add a new order
            Order newOrder = new Order(item.getName(), qty, item.getPrice());
            OrderRepository.getInstance().addOrder(newOrder);
            System.out.println("Added to cart");
            break; // Exit the loop after adding to cart
        }
    }
    
    // Method to display items with a delay
    private void displayItems(List<FoodItem> items) {
        System.out.println("\n----- Menu -----\nID   Name   Price");
        for (FoodItem item : items) {
            System.out.println(item.getId() + "   " + item.getName() + "   $" + item.getPrice());
        }
    }

    // Method to view cart and handle cart menu
    private void viewCart() {
        Scanner scan = new Scanner(System.in);
        System.out.println("\n----- Cart Menu -----");
        List<Order> orders = OrderRepository.getInstance().getAllOrders(); // Get all orders in the cart
        if(orders.isEmpty()){
            System.out.println("----There's no order yet----");
            System.out.println("----------------------------");
            return;
        }
        
        displayOrders(orders); // Display orders
        System.out.println("----------------------------");

        while (true) {
            try {
                System.out.println("\n1. Proceed to Payment\n2. Back to Main Menu");
                System.out.print("\nSelection: ");
                int choice = getIntInput(scan); // Get user input
                scan.nextLine(); // Clear the buffer
                
                switch (choice) {
                    case 1 -> {
                        System.out.println("Proceeding to payment...");
                        printReceipt(orders); // Print receipt and proceed to payment
                    }
                    case 2 -> start(); // Return to main menu
                    default -> System.out.println("Invalid selection. Please choose a number between 1 and 2.");
                }
            } catch (InputMismatchException ex) {
                System.err.println("Invalid Input!");
                scan.nextLine(); // Clear the invalid input
            }
        }
    }

    // Method to display orders in the cart
    private void displayOrders(List<Order> orders) {
        double subtotal = 0.0;
        System.out.println("\n----- Your Cart -----");
        for (Order order : orders) {
            System.out.printf("%-10s x %-3d: $%-7.2f%n", order.getName(), order.getQty(), order.getPrice());
            subtotal += order.getQty() * order.getPrice(); // Calculate subtotal
        }
        
        double tax = calculateTax(subtotal, 0.08); // Calculate tax
        double total = subtotal + tax; // Calculate total
        System.out.println("----------------------");
        System.out.printf("Subtotal: $%.2f%nTax (8%%): $%.2f%nTotal: $%.2f%n", subtotal, tax, total);
    }

    // Method to print receipt and save queueing order
    private void printReceipt(List<Order> orders) {
        int queueingNo = generateQueueingNo(); // Generate queueing number
        String orderCode = generateRandomCode(); // Generate random order code
        Path fileRoot = Paths.get("Storage\\KioskReceipts");
        Path filePath = Paths.get( fileRoot + "\\" + queueingNo + ".txt");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE)) {
            writer.write("-----------------------------\n");
            writer.write("Order No: " + queueingNo + "\n");
            writer.write("-----------------------------\n");

            double subtotal = 0.0;
            for (Order order : orders) {
                writer.write(String.format("%-15s x %-3d: $%-7.2f%n", order.getName(), order.getQty(), order.getPrice()));
                // Save queueing order
                QueueingOrder newOrder = new QueueingOrder(queueingNo, orderCode, order.getName(), order.getQty(), order.getPrice(), false);
                QueueingOrderRepository.getInstance().saveQueueingOrder(newOrder);
                subtotal += order.getQty() * order.getPrice(); // Calculate subtotal
            }

            double tax = calculateTax(subtotal, 0.08); // Calculate tax
            double total = subtotal + tax; // Calculate total
            writer.write("-----------------------------\n");
            writer.write(String.format("Subtotal: $%.2f%n", subtotal));
            writer.write(String.format("Tax (8%%): $%.2f%n", tax));
            writer.write("-----------------------------\n");
            writer.write(String.format("Total: $%.2f%n", total));
            writer.write("-----------------------------\n");
            System.out.println("Receipt has been printed.");
            writer.write("-----------------------------\n");
            OrderRepository.getInstance().clearOrder(); // Clear the cart

            start(); // Go back to main menu
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    
        // Method to generate a random order code
    public String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder(6);
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    // Utility method to get an integer input with validation
    private static int getIntInput(Scanner scan) {
        while (!scan.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scan.next();
        }
        return scan.nextInt();
    }

    // Utility method to calculate tax
    private static double calculateTax(double subtotal, double taxRate) {
        return subtotal * taxRate;
    }

    // Utility method to generate a random queueing number
    private static int generateQueueingNo() {
        return new Random().nextInt(999999);
    }
}
