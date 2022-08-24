package com.example.paperexchange.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "trades")
public class Trade {
    @Id
    @GeneratedValue
    private Long id;
    private Type type;
    private String symbol;
    private double price;
    private int shares;
    private Instant time;
    @ManyToOne
    private User user;

    public Trade() {
    }

    public Trade(Type type, String symbol, double price, int shares, Instant time, User user) {
        this.type = type;
        this.symbol = symbol;
        this.price = price;
        this.shares = shares;
        this.time = time;
        this.user = user;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int quantity) {
        this.shares = quantity;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public enum Type {
        BUY, SELL
    }
}
