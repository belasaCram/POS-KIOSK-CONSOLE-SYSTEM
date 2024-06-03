/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Managers;

import FileSetup.QueueingOrderRepository;
import Objects.QueueingOrder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import poskiosksystem.PosKioskSystem;

/**
 *
 * @author Marc Nelson Belasa
 */
public class CashierManager {
    
    // Main method to start the cashier system
    public void start() {
        Scanner scan = new Scanner(System.in);
        System.out.println("\n--------POS-MACHINE--------");

        while (true) {
            try {
                System.out.println("\n1. See Orders\n2. Exit");
                System.out.print("\nSelection: ");
                int choice = scan.nextInt(); // Get user input
                scan.nextLine(); // Clear the buffer

                switch (choice) {
                    case 1 -> getAllOrderList(); // Display all orders
                    case 2 -> {
                        System.out.println("Thank you for using the POS System. Goodbye!");
                        PosKioskSystem main = new PosKioskSystem();
                        main.startSystem();
                        break;
                    }
                    default -> System.out.println("Invalid Selection!");
                }
            } catch (InputMismatchException ex) {
                System.err.println("Invalid Input!");
                scan.nextLine(); // Clear the invalid input
            }
        }
    }
     
    // Method to get and display all orders
    public void getAllOrderList() {
        Scanner scan = new Scanner(System.in);
        
        System.out.println("\n-----------------------------");
        System.out.println("QueueingNo | Code");
        System.out.println("-----------------------------");

        Set<String> displayedCodes = new HashSet<>();
        // Retrieve and display all unique queueing orders
        for (QueueingOrder order : QueueingOrderRepository.getInstance().getAllQueueingOrder()) {
            if (!displayedCodes.contains(order.getCode())) {
                System.out.println(order.getQueueingNo() + " | " + order.getCode());
                displayedCodes.add(order.getCode());
            }
        }
        
        if(displayedCodes.isEmpty()){
            System.out.println("--- No orders yet ---");
            System.out.println("-----------------------------");

            return;
        }
        
        System.out.println("-----------------------------");

        while (true) {
            System.out.println("1. Checkout\n2. Refresh\n3. Return to main menu");
            try {
                System.out.print("\nSelection: ");
                int choice = scan.nextInt(); // Get user input
                scan.nextLine(); // Clear the buffer

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter Order Code: ");
                        String orderCode = scan.next(); // Get the order code from the user
                        getOrderByCode(orderCode); // Display order details by code
                        return;
                    }
                    case 2 -> getAllOrderList(); // Refresh the list of orders
                    case 3 -> start(); // Return to the main menu
                    default -> System.out.println("Invalid input. Please try again.");
                }
            } catch (InputMismatchException ex) {
                System.err.println("Input Invalid!");
                scan.nextLine(); // Clear the invalid input
            }
        }
    }
    
    // Method to get order details by code
    public void getOrderByCode(String orderCode) {
        List<QueueingOrder> orderList = QueueingOrderRepository.getInstance().getAllQueueingOrderByCode(orderCode);

        if (orderList.isEmpty()) {
            System.out.println("-----------------------------");
            System.out.println("Order with code not found.");
            System.out.println("-----------------------------");
            return;
        }

        String queueingNo = String.valueOf(orderList.get(0).getQueueingNo());
        System.out.println("-----------------------------");
        System.out.println("QueueingNo: " + queueingNo);
        System.out.println("Code: " + orderCode);
        System.out.println("-----------------------------");

        double total = 0.0; // Initialize total
        // Display order details and calculate total
        for (QueueingOrder order : orderList) {
            System.out.printf("%-15s x %-3d: $%-7.2f%n", order.getName(), order.getQty(), order.getPrice());
            total += order.getPrice() * order.getQty() ; // Accumulate the price
        }
        System.out.println("-----------------------------");
        System.out.printf("Total: $%.2f%n", total); // Display the total
        System.out.println("-----------------------------");
        orderApproval(orderList); // Proceed to order approval
    }
    
    // Method to handle order approval
    public void orderApproval(List<QueueingOrder> orders) {
        // Simulate approval logic
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nDo you approve this order? (yes/no): ");

        String approval = scanner.nextLine().trim().toLowerCase();
        if (approval.equals("yes")) {
            System.out.println("Order approved. Processing payment...");
            printReceipt(orders); // Print the receipt
            // Mark order as approved
            for (QueueingOrder order : orders) {
                order.setStatus(true);
            }
        } else {
            System.out.println("Order not approved. Cancelling order...");
            // Remove the order from the queueing system
            for (QueueingOrder order : orders) {
                QueueingOrderRepository.getInstance().deleteQueueingOrder(order.getCode());
            }
        }
    }
    
    // Method to print the receipt of an order
    public void printReceipt(List<QueueingOrder> orders) {
        String code = String.valueOf(orders.get(0).getCode());
        String queueingNo = String.valueOf(orders.get(0).getQueueingNo());
        Path file = Paths.get("Storage\\CashierReceipts");
        Path filePath = Paths.get (file.toAbsolutePath() + "\\" + code + ".txt");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE)) {
            writer.write("-----------------------------\n");
            writer.write("Order No: " + queueingNo + "\n");
            writer.write("-----------------------------\n");

            double total = 0.0;
            for (QueueingOrder order : orders) {
                writer.write(String.format("%-15s x %-3d: $%-7.2f%n", order.getName(), order.getQty(), order.getPrice()));
                total += order.getQty() * order.getPrice();
            }

            writer.write("-----------------------------\n");
            writer.write(String.format("Total: $%.2f%n", total));
            writer.write("-----------------------------\n");
            writer.write("Thank you for your purchase!\n");
            writer.write("-----------------------------\n");
            System.out.println("Receipt has been printed.");

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
        
        QueueingOrderRepository.getInstance().deleteQueueingOrder(orders.get(0).getCode());
        orders.clear();
        deleteKioskReceipt(queueingNo);
    }
    
    private synchronized void deleteKioskReceipt(String fileName){
        Path file = Paths.get("Storage\\KioskReceipts");
        Path filePath = Paths.get(file.toAbsolutePath() + "\\" + fileName + ".txt");
        try{
            Files.deleteIfExists(filePath);
        }catch(IOException ex){
            System.out.println("Cannot find Kiosk Receipt");
        }
    }
}
