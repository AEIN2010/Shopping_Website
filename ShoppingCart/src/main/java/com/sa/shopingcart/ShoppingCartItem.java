package com.sa.shopingcart;



public class ShoppingCartItem{
    public String prodId;
    public int quantity;
    public String updated;
    public String prodName;


    public ShoppingCartItem() {
    }

    public ShoppingCartItem(String prodId, int quantity, String updeted, String name) {
        this.prodId = prodId;
        this.prodName = name;
        this.quantity = quantity;
        this.updated = updeted;
    }
}
