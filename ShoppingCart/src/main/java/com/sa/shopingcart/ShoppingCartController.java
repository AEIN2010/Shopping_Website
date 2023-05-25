package com.sa.shopingcart;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {


    @Autowired
    private RedisService redisService;

    boolean increased = false;
    @ResponseBody
    @PostMapping("/{customerId}")
    ShoppingCart addItemToShoppingCart(@RequestBody ShoppingCartItem item, @PathVariable String customerId){

        ShoppingCart cart = null;
        List<ShoppingCartItem> cartItems = new ArrayList<ShoppingCartItem>();
        try{
            //if the user did not has a cart, a null pointer exception will occur
            cart = new ShoppingCart()
                    .getCart(customerId, StringAndList.StringToList((String) redisService.getValue(customerId)));
        }catch (NullPointerException e){
            cart = new ShoppingCart(customerId, cartItems);
        }
        //in case this user did not have a cart
        cartItems = cart.getProds();
        for(int i = 0; i < cart.getProds().size(); i++){
            if(cartItems.get(i).prodId.equals(item.prodId)){
                cartItems.set(i,new ShoppingCartItem(item.prodId, item.quantity + cartItems.get(i).quantity, item.updated, item.prodName));
                increased = true;
                break;
            }
        }

        if(increased){
            //doNothing
        }else {
            cart.addItem(item, customerId);
        }

        redisService.setValue(cart.customerId, StringAndList.ListToString(cart.getProds()));
        increased = false;
        return cart;
    }


    @GetMapping("/{customerId}")
    @ResponseBody
    ShoppingCart getItemFromShoppingCart(@PathVariable String customerId){
        
        try{
            return new ShoppingCart().getCart(customerId, StringAndList.StringToList((String) redisService.getValue(customerId)));

        }catch (Exception e){
            return new ShoppingCart("This Customer ("+customerId+") Has No Cart Yet");
        }
    }


    @DeleteMapping("/{customerId}/{itemId}")
    @ResponseBody
    ShoppingCart deleteItem(@PathVariable String customerId, @PathVariable String itemId){
        //get shopping cart items from db
        List<ShoppingCartItem> items = StringAndList.StringToList((String) redisService.getValue(customerId));
        for(int i = 0; i < items.size(); i++){
            if(items.get(i).prodId.equals(itemId)){
                items.remove(i);
                break;
            }
        }
        redisService.setValue(customerId,StringAndList.ListToString(items));
        return new ShoppingCart(customerId, items);
    }


    @DeleteMapping("/{customerId}")
    @ResponseBody
    public String deleteCart(@PathVariable("customerId") String customerId){
       
        try{
            redisService.deleteCart(customerId);
            return "Cart of "+customerId+" deleted!";
        }catch (Exception e){
            return "Something Went Wrong. Maybe this customer "+customerId+" has no cart!";
        }
    }
}
