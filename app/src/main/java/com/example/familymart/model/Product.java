package com.example.familymart.model;

public class Product {
    private String key,name,image,price,qrcode;
    private int quantity,remain;

    public Product(){}

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {return quantity;}

    public void setQuantity(int quantity) {this.quantity = quantity;}

    public int getRemain() {return remain;}

    public void setRemain(int remain) {
        this.remain = remain;
    }
}
