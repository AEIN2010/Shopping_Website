package com.sa.shopingcart;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StringAndList {


    public static List<ShoppingCartItem> StringToList(String input) {
        // Remove outer curly braces
        String cleanedInput = input;

        // Split by '},{'
        String[] sets = cleanedInput.split("\\},\\{");

        List<ShoppingCartItem> shoppingCartItems = new ArrayList<>();

        for (String set : sets) {
            // Split by comma
            String[] elements = set.split(",");

            // Create ShoppingCartItem object
            ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
            shoppingCartItem.prodId = elements[0];
            shoppingCartItem.quantity = (Integer.parseInt(elements[1]));
            // Assuming the date is in a specific format, you can parse it accordingly
            // Here's an example using the current date as a placeholder
            shoppingCartItem.updeted = elements[2];

            shoppingCartItems.add(shoppingCartItem);
        }

        return shoppingCartItems;
    }


    public static String ListToString(List<ShoppingCartItem> shoppingCartItems) {
        List<String> sets = new ArrayList<>();

        for (ShoppingCartItem item : shoppingCartItems) {
            String set = item.prodId + "," + item.quantity + "," + item.updeted;
            sets.add(set);
        }

        String joinedSets = String.join("},{", sets);
        return  joinedSets;
    }



}