package com.example.stock_trading_engine.model;

import com.example.stock_trading_engine.enums.OrderType;

public class Order {
    private OrderType orderType;
    private String tickerSymbol;
    private int quantity;
    private double price;

    public Order(OrderType orderType, String tickerSymbol, int quantity, double price) {
        this.orderType = orderType;
        this.tickerSymbol = tickerSymbol;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public OrderType getOrderType() {
        return orderType;
    }
    public String getTickerSymbol() {
        return tickerSymbol;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderType=" + orderType +
                ", tickerSymbol='" + tickerSymbol + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}