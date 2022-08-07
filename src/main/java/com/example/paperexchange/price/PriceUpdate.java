package com.example.paperexchange.price;

public class PriceUpdate {
    private final double price;
    private volatile boolean published = false;

    public PriceUpdate(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public boolean isPublished() {
        return published;
    }

    public void publish() {
        this.published = true;
    }
}
