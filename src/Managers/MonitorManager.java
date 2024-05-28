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
    private final String ORDER_QUEUEING_PATH = "C:\\Users\\Marc Nelson Belasa\\Documents\\NetBeansProjects\\File_Input_And_Output_Java\\PosKioskSystem\\Storage\\QueueingReceipts\\";
    
    public void start() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("-------- MONITOR SYSTEM --------");
            System.out.println("1. View Approved Orders");
            System.out.println("2. Exit");
            try{
                System.out.print("Selection: ");
                int choice = scan.nextInt();
                System.out.println("-----------------------------");

                switch (choice) {
                    case 1 -> displayApprovedOrders();
                    case 2 -> {
                        System.out.println("Exiting Monitor System.");
                        scan.close();
                        return;
                    }
                    default -> System.out.println("Invalid selection. Please choose a number between 1 and 2.");
                }
            }catch(InputMismatchException ex){
                System.err.println("Input invalid!");
            }
        }
    }
    
    private void displayApprovedOrders() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Approved Orders:");
        System.out.println("Code");
        System.out.println("-----------------------------");
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(ORDER_QUEUEING_PATH), "*.txt")) {
            boolean hasFiles = false; 
            for (Path filePath : stream) {
                String fileName = filePath.getFileName().toString();
                String baseName = fileName.split("\\.txt")[0];
                System.out.println(baseName);
                hasFiles = true;
            }
            if (!hasFiles) {
                System.out.println("No approved orders found.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        System.out.println("-----------------------------");
        System.out.println("---Selection----");
        System.out.println("1. Select Order");
        System.out.println("2. Back");
        
        while(true){
            try{
                System.out.print("Selection: ");
                int choice = scan.nextInt();
                switch(choice){
                    case 1 -> orderSelectionByCode(scan);
                    case 2 -> System.out.println("Returing to main menu");
                    default -> System.out.println("Invalid selection! Please choose a number between 1 and 2.");
                }
            }catch(InputMismatchException ex){
                System.err.println("Invalid Input!");
            }
        }
    }
    
    private void orderSelectionByCode(Scanner scan) {
        System.out.print("Enter order code: ");
        String orderCode = scan.next();
        System.out.println("Base name: " + orderCode);
        Thread thread = new Thread(() -> printOrderQueueing(orderCode));
        thread.start();
    }
    
    public void printOrderQueueing(String order){
        Path filePath = Paths.get(ORDER_QUEUEING_PATH + order + ".txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                try {
                    Thread.sleep(500); // Delay for 500 milliseconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    System.out.println("Thread was interrupted.");
                }
            }
        } catch (IOException ex) {
            System.out.println("Order not found or could not read the file.");
            ex.printStackTrace();
        }
    }
}
