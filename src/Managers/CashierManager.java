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

/**
 *
 * @author Marc Nelson Belasa
 */
public class CashierManager {
    
    public void start() {
        Scanner scan = new Scanner(System.in);
        System.out.println("--------POS-MACHINE--------");
        System.out.println("1. See Orders");
        System.out.println("2. Exit");
        while(true){
        
            try{
                System.out.print("Selection: ");
                int choice = scan.nextInt();
                System.out.println("-----------------------------");

                switch (choice) {
                    case 1 -> getAllOrderList();
                    case 2 -> {
                        System.out.println("System exit");
                        return;
                    }
                    default -> System.out.println("Invalid Selection!");
                }
            }catch(InputMismatchException ex){
                System.err.println("Invalid Input!");
            }
        
        }
    }
     
    public void getAllOrderList() {
        System.out.println("QueueingNo | Code");
        System.out.println("-----------------------------");

        Set<String> displayedCodes = new HashSet<>();

        for (QueueingOrder order : QueueingOrderRepository.getInstance().getAllQueueingOrder()) {
            if (!displayedCodes.contains(order.getCode())) {
                System.out.println(order.getQueueingNo() + " | " + order.getCode());
                displayedCodes.add(order.getCode());
            }
        }

        System.out.println("-----------------------------");

        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.println("1. Checkout");
            System.out.println("2. Refresh");
            System.out.println("3. Return to main menu");
            try{
                System.out.print("Selection: ");
                int choice = scan.nextInt();
                scan.nextLine();

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter Order Code: ");
                        String orderCode = scan.next();
                        getOrderByCode(orderCode);
                        return;
                    }
                    case 2 -> getAllOrderList();
                    case 3 -> start();
                    default -> System.out.println("Invalid input. Please try again.");
                }
            }catch(InputMismatchException ex){
                System.err.println("Input Invalid!");
            }
        }
    }
    
    public void getOrderByCode(String orderCode) {
        List<QueueingOrder> orderList = QueueingOrderRepository.getInstance().getAllQueueingOrderByCode(orderCode);

        if (orderList.isEmpty()) {
            System.out.println("-----------------------------");
            System.out.println("Order with code " + orderCode + " not found.");
            System.out.println("-----------------------------");
            return;
        }

        String queueingNo = String.valueOf(orderList.get(0).getQueueingNo());
        
        System.out.println("-----------------------------");
        System.out.println("QueueingNo: " + queueingNo);
        System.out.println("Code: " + orderCode);
        System.out.println("-----------------------------");

        for (QueueingOrder order : orderList) {
            System.out.printf("%-15s x %-3d: $%-7.2f%n", order.getName(), order.getQty(), order.getPrice());
        }
        System.out.println("-----------------------------");

        orderApproval(orderList);
    }
    
    public void orderApproval(List<QueueingOrder> orders) {
        // Simulate approval logic
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you approve this order? (yes/no): ");

        String approval = scanner.nextLine().trim().toLowerCase();
        if (approval.equals("yes")) {
            System.out.println("Order approved. Processing payment...");
            printReceipt(orders);
            // Mark order as approved
            for (QueueingOrder order : orders) {
                order.setStatus(true);
            }
        } else {
            System.out.println("Order not approved. Cancelling order...");
            // Remove the order from the queueing system
            for (QueueingOrder order : orders) {
                QueueingOrderRepository.getInstance().DeleteQueueingOrder(order.getCode());
            }
        }
    }
    
    public void printReceipt(List<QueueingOrder> orders) {
        String queueingNo = String.valueOf(orders.get(0).getQueueingNo());
        Path filePath = Paths.get("C:\\Users\\Marc Nelson Belasa\\Documents\\NetBeansProjects\\File_Input_And_Output_Java\\KioskSystem\\Database\\QueueingReceipts\\" + queueingNo + ".txt");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE)) {
            writer.write("-----------------------------\n");
            writer.write("Order No: " + queueingNo + "\n");
            writer.write("-----------------------------\n");

            double subtotal = 0.0;
            for (QueueingOrder order : orders) {
                writer.write(String.format("%-15s x %-3d: $%-7.2f%n", order.getName(), order.getQty(), order.getPrice()));
                subtotal += order.getQty() * order.getPrice();
            }

            double tax = calculateTax(subtotal, 0.08);
            double total = subtotal + tax;

            writer.write(String.format("Subtotal: $%.2f%n", subtotal));
            writer.write(String.format("Tax (8%%): $%.2f%n", tax));
            writer.write(String.format("Total: $%.2f%n", total));
            writer.write("-----------------------------\n");
            writer.write("Thank you for your purchase!\n");
            writer.write("-----------------------------\n");

            System.out.println("Receipt has been printed.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    
    private static double calculateTax(double subtotal, double taxRate) {
        return subtotal * taxRate;
    }
}
