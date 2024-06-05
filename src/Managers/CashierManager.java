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
    
    private final Scanner scan = new Scanner(System.in);

    // Main method to start the cashier system
    public void start() {
        System.out.println("\n--------POS-MACHINE--------");

        while (true) {
            try {
                System.out.println("\n1. See Orders\n2. Exit");
                System.out.print("Selection: ");
                int choice = scan.nextInt();
                scan.nextLine(); // Clear the buffer

                switch (choice) {
                    case 1 -> getAllOrderList();
                    case 2 -> exitSystem();
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
        System.out.println("\n-----------------------------");
        System.out.println("QueueingNo | Code");
        System.out.println("-----------------------------");

        Set<String> displayedCodes = new HashSet<>();
        QueueingOrderRepository repo = QueueingOrderRepository.getInstance();

        for (QueueingOrder order : repo.getAllQueueingOrder()) {
            if (displayedCodes.add(order.getCode())) {
                System.out.println(order.getQueueingNo() + " | " + order.getCode());
            }
        }

        if (displayedCodes.isEmpty()) {
            System.out.println("--- No orders yet ---");
        }
        System.out.println("-----------------------------");

        manageOrders();
    }

    // Method to manage orders
    private void manageOrders() {
        while (true) {
            System.out.println("1. Checkout\n2. Refresh\n3. Return to main menu");
            try {
                System.out.print("Selection: ");
                int choice = scan.nextInt();
                scan.nextLine(); // Clear the buffer

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter Order Code: ");
                        getOrderByCode(scan.next());
                        return;
                    }
                    case 2 -> getAllOrderList();
                    case 3 -> start();
                    default -> System.out.println("Invalid input. Please try again.");
                }
            } catch (InputMismatchException ex) {
                System.err.println("Invalid Input!");
                scan.nextLine(); // Clear the invalid input
            }
        }
    }

    // Method to display order details
    private void displayOrderDetails(List<QueueingOrder> orderList) {
        String queueingNo = String.valueOf(orderList.get(0).getQueueingNo());
        System.out.println("-----------------------------");
        System.out.println("QueueingNo: " + queueingNo);
        System.out.println("Code: " + orderList.get(0).getCode());
        System.out.println("-----------------------------");

        double total = 0.0;
        for (QueueingOrder order : orderList) {
            System.out.printf("%-15s x %-3d: $%-7.2f%n", order.getName(), order.getQty(), order.getPrice());
            total += order.getPrice() * order.getQty();
        }
        System.out.println("-----------------------------");
        System.out.printf("Total: $%.2f%n", total);
        System.out.println("-----------------------------");
    }

    // Method to handle order approval
    public void orderApproval(List<QueueingOrder> orders) {
        System.out.println("Do you approve this order? (yes)");
        System.out.println("Any other input will cancel the order. To go back, type 'back'.");
        System.out.print("Input: ");

        String approval = scan.nextLine().trim().toLowerCase();

        switch (approval) {
            case "yes" -> approveOrder(orders);
            case "back" -> getAllOrderList();
            default -> cancelOrder(orders);
        }
    }

    // Method to print the receipt of an order
    public void printReceipt(List<QueueingOrder> orders) {
        String code = orders.get(0).getCode();
        Path filePath = Paths.get("Storage/CashierReceipts/" + code + ".txt");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE)) {
            writer.write("-----------------------------\n");
            writer.write("Order No: " + orders.get(0).getQueueingNo() + "\n");
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

        QueueingOrderRepository.getInstance().deleteQueueingOrder(code);
        orders.clear();
        deleteKioskReceipt(orders.get(0).getQueueingNo());
    }
    
    //region Utilities

    // Method to delete kiosk receipt
    private synchronized void deleteKioskReceipt(int queueingNo) {
        Path filePath = Paths.get("Storage/KioskReceipts/" + queueingNo + ".txt");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            System.err.println("Cannot find Kiosk Receipt");
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

        displayOrderDetails(orderList);
        orderApproval(orderList);
    }

    // Method to approve order
    private void approveOrder(List<QueueingOrder> orders) {
        System.out.println("Order approved. Processing payment...");
        printReceipt(orders);
        orders.forEach(order -> order.setStatus(true));
    }

    // Method to cancel order
    private void cancelOrder(List<QueueingOrder> orders) {
        System.out.println("Order not approved. Cancelling order...");
        QueueingOrderRepository repo = QueueingOrderRepository.getInstance();
        orders.forEach(order -> repo.deleteQueueingOrder(order.getCode()));
    }

    // Method to exit the system
    private void exitSystem() {
        System.out.println("Thank you for using the POS System. Goodbye!");
        PosKioskSystem main = new PosKioskSystem();
        main.startSystem();
    }

    //endregion
}
