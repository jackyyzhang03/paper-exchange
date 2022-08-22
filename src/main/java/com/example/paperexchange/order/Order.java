package com.example.paperexchange.order;

import com.example.paperexchange.user.User;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String symbol;
    @Column(nullable = false)
    private Type type;
    @Column(nullable = false)
    private int shares;
    @ManyToOne
    private User user;
    private double executionPrice; // Not applicable for market orders
    private double stopLimitPrice; // Only applicable for stop limit orders
    @Column(nullable = false)
    private boolean sell;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int quantity) {
        this.shares = quantity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(double executionPrice) {
        this.executionPrice = executionPrice;
    }

    public double getStopLimitPrice() {
        return stopLimitPrice;
    }

    public void setStopLimitPrice(double stopLimitPrice) {
        this.stopLimitPrice = stopLimitPrice;
    }

    public boolean isSell() {
        return sell;
    }

    public void setSell(boolean sell) {
        this.sell = sell;
    }

    public enum Type {
        MARKET, LIMIT, STOP, STOP_LIMIT
    }
}
