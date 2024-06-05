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
    // Reference to the repository that handles food items
    private final IFoodItemRepository itemRepo;
    // Counter to keep track of item indexes in the cart
    private int ITEM_INDEX = 0;

    // Constructor to initialize the item repository
    public KioskManager(IFoodItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    // Main method to start the kiosk system
    public void start() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            // Display the main menu options to the user
            System.out.println("\nWelcome to the Kiosk System!");
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Choose Meal");
            System.out.println("2. View Cart");
            System.out.println("3. Exit");
            System.out.print("\nSelection: ");

            // Get user input and handle menu selection
            int choice = getIntInput(scan);
            switch (choice) {
                case 1 -> chooseCategory(); // Option to choose a food category
                case 2 -> viewCart(); // Option to view the cart
                case 3 -> exitSystem(); // Option to exit the system
                default -> System.out.println("Invalid selection. Please choose a number between 1 and 3.");
            }
        }
    }

    // Method to handle the selection of food category
    private void chooseCategory() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            // Display the category menu options to the user
            System.out.println("\n--- Category Menu ---");
            System.out.println("1. Dessert");
            System.out.println("2. Chicken");
            System.out.println("3. Drinks");
            System.out.println("4. Return to Main Menu");
            System.out.print("\nSelection: ");

            // Get user input and handle category selection
            int choice = getIntInput(scan);
            if (choice == 4) return; // Return to main menu
            if (choice < 1 || choice > 3) {
                System.out.println("Invalid selection. Please choose a number between 1 and 4.");
                continue;
            }

            // Proceed to choose a specific item within the selected category
            chooseItem(choice);
        }
    }

    // Method to handle the selection of a specific food item
    private void chooseItem(int categoryId) {
        Scanner scan = new Scanner(System.in);
        // Get the list of items for the selected category
        List<FoodItem> items = itemRepo.getFoodItemsByCategory(categoryId);
        // Display the list of items to the user
        displayItems(items);

        System.out.println("\nIf you want to go back type 0.");
        System.out.print("Selection: ");
        int itemId = getIntInput(scan); // Get user input for item selection
        if (itemId == 0) return; // Go back to the previous menu if the user enters 0

        // Find the selected item based on the item ID
        Optional<FoodItem> itemOpt = items.stream().filter(item -> item.getId() == itemId).findFirst();
        if (!itemOpt.isPresent()) {
            System.err.println("Invalid item ID. Please select from the given list.");
            return;
        }

        // Get the quantity of the selected item
        System.out.print("Enter the quantity: ");
        int qty = getIntInput(scan);
        
        if(qty < 1 ){
            System.out.println("Quantity must be 1 or more. Please enter a valid quantity.");
            return;
        }
        
        // Increment the item index for the order
        ITEM_INDEX++;
        // Create a new order and add it to the order repository
        Order newOrder = new Order(ITEM_INDEX, itemOpt.get().getName(), qty, itemOpt.get().getPrice());
        OrderRepository.getInstance().addOrder(newOrder);
        System.out.println("Added to cart");
    }

    // Method to display a list of food items
    private void displayItems(List<FoodItem> items) {
        System.out.println("\n----- Menu -----\nID   Name   Price");
        // Loop through each item and print its details
        for (FoodItem item : items) {
            System.out.println(item.getId() + "   " + item.getName() + "   $" + item.getPrice());
        }
    }

    // Method to handle the viewing of the cart
    private void viewCart() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("\n----- Cart Menu -----");
            // Get the list of orders in the cart
            List<Order> orders = OrderRepository.getInstance().getAllOrders();
            if (orders.isEmpty()) {
                System.out.println("----There's no order yet----");
                return;
            }

            // Display the orders in the cart
            displayOrders(orders);
            System.out.println("----------------------------");
            System.out.println("\n1. Proceed to Payment\n2. Delete Order\n3. Back to Main Menu");
            System.out.print("\nSelection: ");

            // Get user input and handle cart menu selection
            int choice = getIntInput(scan);
            switch (choice) {
                case 1 -> proceedToPayment(orders); // Proceed to payment
                case 2 -> deleteOrder(scan); // Delete an order from the cart
                case 3 -> start(); // Return to the main menu
                default -> System.out.println("Invalid selection. Please choose a number between 1 and 3.");
            }
        }
    }

    // Method to display the orders in the cart
    private void displayOrders(List<Order> orders) {
        double total = 0.0;
        System.out.println("\n----- Your Cart -----");
        System.out.println("ID : ORDER NAME : QTY : PRICE");
        // Loop through each order and print its details
        for (Order order : orders) {
            System.out.printf("%-3d: %-10s x %-3d: $%-7.2f%n", order.getId(), order.getName(), order.getQty(), order.getPrice());
            total += order.getQty() * order.getPrice();
        }
        System.out.println("----------------------");
        System.out.printf("Total: $%.2f%n", total);
    }

    // Method to handle the payment process
    private void proceedToPayment(List<Order> orders) {
        int queueingNo = generateQueueingNo(); // Generate a queueing number
        String orderCode = generateRandomCode(); // Generate a random order code
        Path fileRoot = Paths.get("Storage\\KioskReceipts");
        Path filePath = Paths.get(fileRoot.toAbsolutePath() + "\\" + queueingNo + ".txt");

        // Write the receipt to a file
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE)) {
            writer.write("-----------------------------\n");
            writer.write("Order No: " + queueingNo + "\n");
            writer.write("Order Code: " + orderCode + "\n");
            writer.write("-----------------------------\n");

            double total = 0.0;
            // Loop through each order and write its details to the file
            for (Order order : orders) {
                writer.write(String.format("%-15s x %-3d: $%-7.2f%n", order.getName(), order.getQty(), order.getPrice()));
                QueueingOrder newOrder = new QueueingOrder(queueingNo, orderCode, order.getName(), order.getQty(), order.getPrice(), false);
                QueueingOrderRepository.getInstance().saveQueueingOrder(newOrder);
                total += order.getQty() * order.getPrice();
            }

            writer.write("-----------------------------\n");
            writer.write(String.format("Total: $%.2f%n", total));
            writer.write("-----------------------------\n");
            System.out.println("Receipt has been printed.");
            writer.flush();

            OrderRepository.getInstance().clearOrder(); // Clear the cart
            start(); // Go back to the main menu
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    //region Utilities

    // Method to delete an order from the cart
    private void deleteOrder(Scanner scan) {
        System.out.print("Select order by Id to delete: ");
        int orderId = scan.nextInt();
        System.err.println(OrderRepository.getInstance().deleteOrderById(orderId));
    }

    // Method to exit the kiosk system
    private void exitSystem() {
        System.out.println("Thank you for using the Kiosk System. Goodbye!");
        PosKioskSystem main = new PosKioskSystem();
        main.startSystem();
    }

    // Method to generate a random order code
    private String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder(6);
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    // Method to get a valid integer input from the user
    private static int getIntInput(Scanner scan) {
        while (!scan.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scan.next();
        }
        return scan.nextInt();
    }

    // Method to generate a queueing number
    private static int generateQueueingNo() {
        return new Random().nextInt(999);
    }

    //endregion Utilities
}
