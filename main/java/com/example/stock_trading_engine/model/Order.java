package com.example.stock_trading_engine.model;

import com.example.stock_trading_engine.enums.OrderType;

import java.util.UUID;

public class Order {
    private final String id;
    private Order next;
    private final OrderType orderType;
    private final String tickerSymbol;
    private int quantity;
    private final double price;

    public Order(OrderType orderType, String tickerSymbol, int quantity, double price) {
        this.id = generateUniqueId(); // Assign a unique ID
        this.orderType = orderType;
        this.tickerSymbol = tickerSymbol;
        this.quantity = quantity;
        this.price = price;
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString(); // Example of generating a unique ID
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public OrderType getOrderType() {
        return orderType;
    }
    public String getTickerSymbol() {
        return tickerSymbol;
    }
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public Order getNext() {
        return next;
    }

    public void setNext(Order next) {
        this.next = next;
    }


    @Override
    public String toString() {
        return String.format("Type: %s, Ticker: %s, Quantity: %d, Price: $%.2f", orderType, tickerSymbol, quantity, price);
    }
}