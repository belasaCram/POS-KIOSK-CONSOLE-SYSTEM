package Objects;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Marc Nelson Belasa
 */
public class Order {
    private int Id;
    private String name;
    private int qty;
    private double price;

    public Order(int id, String name, int qty, double price){
        this.Id = id;
        this.name = name;
        this.qty = qty;
        this.price = price;
    }

    public int getId() { return Id; }
    public String getName() { return name; }
    public int getQty() { return qty; }
    public double getPrice() { return price; }
}
