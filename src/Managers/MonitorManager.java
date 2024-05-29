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
     // Path to the directory containing order queueing receipts
    private final String ORDER_QUEUEING_PATH = "C:\\Users\\Marc Nelson Belasa\\Documents\\NetBeansProjects\\File_Input_And_Output_Java\\PosKioskSystem\\Storage\\QueueingReceipts\\";
    
    // Main method to start the monitor system
    public void start() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("\n-------- MONITOR SYSTEM --------");
            System.out.println("1. View Approved Orders");
            System.out.println("2. Exit");
            try {
                System.out.print("Selection: ");
                int choice = scan.nextInt(); // Get user input
                System.out.println("-----------------------------");

                switch (choice) {
                    case 1 -> displayApprovedOrders(); // Display approved orders
                    case 2 -> {
                        System.out.println("Exiting Monitor System.");
                        return; // Exit the system
                    }
                    default -> System.err.println("Invalid selection. Please choose a number between 1 and 2.");
                }
            } catch (InputMismatchException ex) {
                System.err.println("Input invalid!");
                scan.nextLine(); // Clear the invalid input
            }
        }
    }
    
    // Method to display approved orders
    private void displayApprovedOrders() {
        Scanner scan = new Scanner(System.in);
        System.out.println("\n-----------------------------");
        System.out.println("Approved Orders:");
        System.out.println("Code");
        System.out.println("-----------------------------");
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(ORDER_QUEUEING_PATH), "*.txt")) {
            boolean hasFiles = false; 
            for (Path filePath : stream) {
                String fileName = filePath.getFileName().toString();
                String baseName = fileName.split("\\.txt")[0]; // Extract base name of the file
                System.out.println(baseName);
                hasFiles = true;
            }
            if (!hasFiles) {
                System.out.println("No approved orders found.");
            }
        } catch (IOException ex) {
            System.err.println("Error on finding the files");
        }
        
        System.out.println("-----------------------------");
        
        while (true) {
            try {
                System.out.println("---Selection----");
                System.out.println("1. Select Order");
                System.out.println("2. Back");
                System.out.print("Selection: ");
                int choice = scan.nextInt(); // Get user input
                scan.nextLine(); // Clear the buffer

                switch (choice) {
                    case 1 -> orderSelectionByCode(scan); // Select order by code
                    case 2 -> {
                        System.out.println("Returning to main menu");
                        start(); // Return to main menu
                    }
                    default -> System.err.println("Invalid selection! Please choose a number between 1 and 2.");
                }
            } catch (InputMismatchException ex) {
                System.err.println("Invalid Input!");
                scan.nextLine(); // Clear the invalid input
            }
        }
    }
    
    // Method to handle order selection by code
    private void orderSelectionByCode(Scanner scan) {
        System.out.print("Enter order code: ");
        String orderCode = scan.next(); // Get order code from user
        System.out.println("Base name: " + orderCode);
        Thread thread = new Thread(() -> printOrderQueueing(orderCode)); // Print order in a separate thread
        thread.start();
    }
    
    // Method to print order details from a file
    public void printOrderQueueing(String order) {
        Path filePath = Paths.get(ORDER_QUEUEING_PATH + order + ".txt");    

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                try {
                    Thread.sleep(1000); // Delay for 1 second between lines
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    System.err.println("Thread was interrupted.");
                }
            }
        } catch (IOException ex) {
            System.err.println("Order not found or could not read the file.");
        }
    }
}
