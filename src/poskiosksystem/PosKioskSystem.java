/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package poskiosksystem;

import FileSetup.FoodItemRepository;
import FileSetup.IFoodItemRepository;
import Managers.CashierManager;
import Managers.KioskManager;
import Managers.MonitorManager;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author Marc Nelson Belasa
 */
public class PosKioskSystem {

    public static void main(String[] args) {
    
        PosKioskSystem main = new PosKioskSystem();
        main.startSystem();
    }
    
    public void startSystem(){
        Scanner scan = new Scanner(System.in);
        IFoodItemRepository itemRepo = new FoodItemRepository();
        KioskManager kioskSystem = new KioskManager(itemRepo);
        CashierManager cashierSystem = new CashierManager();
        MonitorManager monitorSystem = new MonitorManager();
        
        while (true) {
            try{
                System.out.println("\nWelcome to the Customer End System!");
                System.out.println("1. Kiosk System");
                System.out.println("2. Cashier System");
                System.out.println("3. Monitor System");
                System.out.println("4. Exit");
                System.out.print("\nSelection: ");
                int choice = scan.nextInt();
                scan.nextLine();

                switch (choice) {
                    case 1 ->  kioskSystem.start();
                    case 2 -> cashierSystem.start();
                    case 3 -> monitorSystem.start();
                    case 4 -> {
                        System.out.println("System shutdown!");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid selection. Please choose a number between 1 and 4.");
                }
            
            }catch(InputMismatchException ex){
                System.err.println("Invalid Input!");
                scan.nextLine();
            }
        }
    
    }
}
