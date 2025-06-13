package com.example.familymart.listener;

import com.example.familymart.model.Cart;

import java.util.List;

public interface CartLoadListener {
    void onCartLoadSuccess(List<Cart> cartList);
    void onCartLoadFailed(String message);
}
