/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package FileSetup;

import Objects.FoodItem;
import java.util.List;

/**
 *
 * @author Marc Nelson Belasa
 */
public interface IFoodItemRepository {
    public List<FoodItem> getFoodItemsByCategory(int categoryId);
    public FoodItem getFoodItemById(int id);
}
