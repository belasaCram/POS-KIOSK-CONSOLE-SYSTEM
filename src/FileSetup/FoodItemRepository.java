/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileSetup;

import Objects.FoodItem;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marc Nelson Belasa
 */
public class FoodItemRepository implements IFoodItemRepository{
    private final List<FoodItem> items;

    public FoodItemRepository() {
        items = new ArrayList<>();
        items.add(new FoodItem(1, "Ice Cream", 50, 1));
        items.add(new FoodItem(2, "Halo-Halo", 30, 1));
        items.add(new FoodItem(3, "Sundae", 20, 1));
        items.add(new FoodItem(4, "Mc Chicken", 190, 2));
        items.add(new FoodItem(5, "Chicken Fillet", 190, 2));
        items.add(new FoodItem(6, "Burger Steak", 190, 2));
        items.add(new FoodItem(7, "Coke Float", 190, 3));
        items.add(new FoodItem(8, "Regular Sprite", 190, 3));
        items.add(new FoodItem(9, "Regular Nestea", 190, 3));
    }

    //region Functions

    public List<FoodItem> getFoodItemsByCategory(int categoryId) {
        List<FoodItem> filteredFoodItems = new ArrayList<>();
        for (FoodItem item : items) {
            if (item.getCategoryId() == categoryId) {
                filteredFoodItems.add(item);
            }
        }
        return filteredFoodItems;
    }

    public FoodItem getFoodItemById(int id) {
        for (FoodItem item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    //endregion
}
