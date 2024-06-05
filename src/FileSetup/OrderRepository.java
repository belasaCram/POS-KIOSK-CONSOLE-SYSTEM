/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileSetup;

import Objects.Order;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Marc Nelson Belasa
 */
public class OrderRepository {

    private static OrderRepository instance;
    private final List<Order> orders;

    private OrderRepository() {
        orders = new ArrayList<>();
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }
    
    //region Functions

    public List<Order> getAllOrders() {
        return Collections.unmodifiableList(orders);
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void clearOrder() {
        orders.clear();
    }

    public synchronized String deleteOrderById(int id){
        for(Order order : orders){
            if(order.getId() == id){
                orders.remove(order);
                return "Order is removed..";
            }
        }
        return "Cannot find inputed Id..";
    }

    //endregion
}
