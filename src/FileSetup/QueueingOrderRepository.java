/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileSetup;

import Objects.QueueingOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Marc Nelson Belasa
 */
public class QueueingOrderRepository {
    private static QueueingOrderRepository instance;
    private List<QueueingOrder> queueingOrder;
    
    private QueueingOrderRepository(){
        queueingOrder = new ArrayList<>();
    }
    
    public static synchronized QueueingOrderRepository getInstance(){
        if(instance == null){
            instance = new QueueingOrderRepository();
        }
        return instance;
    }
    
    public List<QueueingOrder> getAllQueueingOrder(){
        return Collections.unmodifiableList(queueingOrder);
    }
    
    public List<QueueingOrder> getAllQueueingOrderByCode(String orderCode){
        List<QueueingOrder> orders = new ArrayList<>();
        for(QueueingOrder order : queueingOrder){
            if(order.getCode().equals(orderCode)){
                orders.add(order);
            }
        }
        return orders;
    }

    public void saveQueueingOrder(QueueingOrder order){
        queueingOrder.add(order);
    }
    
    public void DeleteQueueingOrder(String orderCode){
        for(QueueingOrder order : queueingOrder){
            if(order.getCode().equals(orderCode)){
                queueingOrder.remove(order);
            }
        }
    }
}
