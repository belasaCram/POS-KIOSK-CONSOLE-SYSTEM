/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Managers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author Marc Nelson Belasa
 */
public class MonitorManager {

    private final Path FILE_ROOT = Paths.get("Storage\\CashierReceipts");
    
    // Main method to start the monitor system
    public void start() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("\n-------- MONITOR SYSTEM --------");
            System.out.println("1. View Approved Orders");
            System.out.println("2. Exit");
            try {
                System.out.print("\nSelection: ");
                int choice = scan.nextInt(); // Get user input

                switch (choice) {
                    case 1 -> displayApprovedOrders(); // Display approved orders
                    case 2 -> {
                        System.out.println("\nExiting Monitor System.");
                        return; // Exit the system
                    }
                    default -> System.err.println("\nInvalid selection. Please choose a number between 1 and 2.");
                }
            } catch (InputMismatchException ex) {
                System.err.println("\nInput invalid!");
                scan.nextLine(); // Clear the invalid input
            }
        }
    }
    
    private void displayApprovedOrders() {
        System.out.println("\n-----------------------------");
        System.out.println("Approved Orders:");
        System.out.println("Code");
        System.out.println("-----------------------------");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(FILE_ROOT.toAbsolutePath(), "*.txt")) {
            boolean hasFiles = false;
            for (Path filePath : stream) {
                String fileName = filePath.getFileName().toString();
                String baseName = fileName.replaceFirst("\\.txt$", ""); // Extract base name of the file
                System.out.println(baseName);
                hasFiles = true;
            }
            if (!hasFiles) {
                System.out.println("No approved orders found.");
            }
        } catch (IOException ex) {
            System.err.println("Error finding the order!");
        }
        System.out.println("-----------------------------");
        orderSelectionByCode();
    }

    // Method to handle order selection by code
    private synchronized void orderSelectionByCode() {
        Scanner scan = new Scanner(System.in);
        System.out.println("If you want to go back type exit");
        System.out.print("Enter order code: ");
        String orderCode = scan.next(); // Get order code from user
        
        if (orderCode.equalsIgnoreCase("exit")) {
            start();
        }
        
        while(true){
            try{
                Path filePath = printOrderQueueing(orderCode);
                System.out.println("1. Serve Order\n2. Back\n");
                System.out.print("Selection: ");
                int choice = scan.nextInt();
                scan.nextLine();

                switch(choice){
                    case 1 -> {
                        Files.deleteIfExists(filePath);
                        System.out.println("Order is served...");
                        displayApprovedOrders();
                    }
                    case 2 -> {
                        System.out.println("Going back to main menu...");
                        start();
                        break;
                    }
                    default -> System.out.println("Invalid input!");
                }
            }catch(IOException ex){
                System.err.println("Having a trouble in serving an order....");
            }catch(InputMismatchException ex){
                System.err.println("Invalid Input!");
                scan.nextLine();
            }
        }
    }

    // Method to print order details from a file
    public synchronized Path printOrderQueueing(String order) {
        Path filePath = FILE_ROOT.resolve(order + ".txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            System.err.println("Order not found....");
        }
        return filePath;
    }
}
