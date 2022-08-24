package com.example.paperexchange.entities;

import javax.persistence.*;

@Entity
@Table(name = "holdings")
public class Holding {
    @Id
    @GeneratedValue
    private Long id;
    private String symbol;
    private int shares;
    private double adjustedCostBase;
    @ManyToOne
    private User user;

    public Holding() {
    }

    public Holding(String symbol, int shares, double adjustedCostBase, User user) {
        this.symbol = symbol;
        this.shares = shares;
        this.adjustedCostBase = adjustedCostBase;
        this.user = user;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public double getAdjustedCostBase() {
        return adjustedCostBase;
    }

    public void setAdjustedCostBase(double adjustedCostBase) {
        this.adjustedCostBase = adjustedCostBase;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
