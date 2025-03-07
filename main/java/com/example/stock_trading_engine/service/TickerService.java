package com.example.stock_trading_engine.service;

import com.example.stock_trading_engine.model.Order;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Random;

@Service
public class TickerService {

    private static final int NUM_TICKERS = 1024;
    private final OrderBook[] orderBooks = new OrderBook[NUM_TICKERS];

    public TickerService(Random random) {
        for (int i = 0; i < NUM_TICKERS; i++) {
            orderBooks[i] = new OrderBook();
        }
    }

    public void addOrder(Order order) {
        int tickerIndex = getTickerIndex(order.getTickerSymbol());
        OrderBook orderBook = orderBooks[tickerIndex];

        if (order.getOrderType() == com.example.stock_trading_engine.enums.OrderType.BUY) {
            orderBook.addBuyOrder(order);
        } else {
            orderBook.addSellOrder(order);
        }

        matchOrder(order.getTickerSymbol());
    }

    public void matchOrder(String tickerSymbol) {
        int tickerIndex = getTickerIndex(tickerSymbol);
        OrderBook orderBook = orderBooks[tickerIndex];

        Order sellOrder = orderBook.sellOrders.get();
        Order buyOrder = orderBook.buyOrders.get();

        if (buyOrder != null && sellOrder != null && buyOrder.getPrice() >= sellOrder.getPrice()) {
            System.out.println("Matched Order: Ticker = " + tickerSymbol +
                    ", Buy Price = $" + buyOrder.getPrice() +
                    ", Sell Price = $" + sellOrder.getPrice() +
                    ", Quantity = " + Math.min(buyOrder.getQuantity(), sellOrder.getQuantity()));
            orderBook.sellOrders.compareAndSet(sellOrder, null);
            orderBook.buyOrders.compareAndSet(buyOrder, null);
        }
    }

    private int getTickerIndex(String tickerSymbol) {
        return Math.abs(tickerSymbol.hashCode()) % NUM_TICKERS;
    }

    private static class OrderBook {
        private final AtomicReference<Order> buyOrders = new AtomicReference<>();
        private final AtomicReference<Order> sellOrders = new AtomicReference<>();

        public void addBuyOrder(Order order) {
            buyOrders.set(order);
        }

        public void addSellOrder(Order order) {
            sellOrders.set(order);
        }
    }
}