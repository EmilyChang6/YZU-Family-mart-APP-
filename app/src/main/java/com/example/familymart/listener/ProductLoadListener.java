package com.example.familymart.listener;

import com.example.familymart.model.Product;

import java.util.List;

public interface ProductLoadListener {
    void onProductLoadSuccess(List<Product> productList);
    void onProductLoadFailed(String message);
}
