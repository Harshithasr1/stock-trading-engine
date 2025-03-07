package com.example.stock_trading_engine.service;

import com.example.stock_trading_engine.model.Order;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TickerService {

    private static final int NUM_TICKERS = 1024;
    private final OrderBook[] orderBooks = new OrderBook[NUM_TICKERS];

    public TickerService() {
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
        orderBook.matchOrders();
    }

    public Order[] getOrderBook(String tickerSymbol) {
        int tickerIndex = getTickerIndex(tickerSymbol);
        OrderBook orderBook = orderBooks[tickerIndex];
        return orderBook.getAllOrders();
    }

    public Order[] getMatchedOrders(String tickerSymbol) {
        int tickerIndex = getTickerIndex(tickerSymbol);
        OrderBook orderBook = orderBooks[tickerIndex];
        return orderBook.getMatchedOrders();
    }

    public Order[] getAllOrdersFromOrderBook(int index) {
        if (index >= 0 && index < NUM_TICKERS) {
            return orderBooks[index].getAllOrders();
        }
        return null;
    }

    public Order[] getAllMatchedOrdersFromOrderBook(int index) {
        if (index >= 0 && index < NUM_TICKERS) {
            return orderBooks[index].getMatchedOrders();
        }
        return null;
    }

    private int getTickerIndex(String tickerSymbol) {
        return Math.abs(tickerSymbol.hashCode()) % NUM_TICKERS;
    }

    private static class OrderBook {
        // Lock-free linked lists for buy and sell orders.
        private final AtomicReference<Order> buyOrders = new AtomicReference<>(null);
        private final AtomicReference<Order> sellOrders = new AtomicReference<>(null);
        private final AtomicReference<Order> matchedOrdersList = new AtomicReference<>(null); // Track matched orders

        public void addBuyOrder(Order order) {
            Order oldHead;
            do {
                oldHead = buyOrders.get();
                order.setNext(oldHead);
            } while (!buyOrders.compareAndSet(oldHead, order));
        }

        public void addSellOrder(Order order) {
            Order oldHead;
            do {
                oldHead = sellOrders.get();
                order.setNext(oldHead);
            } while (!sellOrders.compareAndSet(oldHead, order));
        }

        public void matchOrders() {
            // Find the highest buy order.
            Order highestBuy = null;
            Order currentBuy = buyOrders.get();
            while (currentBuy != null) {
                if (highestBuy == null || currentBuy.getPrice() > highestBuy.getPrice()) {
                    highestBuy = currentBuy;
                }
                currentBuy = currentBuy.getNext();
            }

            // Find the lowest sell order.
            Order lowestSell = null;
            Order currentSell = sellOrders.get();
            while (currentSell != null) {
                if (lowestSell == null || currentSell.getPrice() < lowestSell.getPrice()) {
                    lowestSell = currentSell;
                }
                currentSell = currentSell.getNext();
            }

            // Match orders if the highest buy price is >= the lowest sell price.
            if (highestBuy != null && lowestSell != null && highestBuy.getPrice() >= lowestSell.getPrice()) {
                // Capture pre-update quantities
                int buyQtyBefore = highestBuy.getQuantity();
                int sellQtyBefore = lowestSell.getQuantity();
                int matchedQuantity = Math.min(buyQtyBefore, sellQtyBefore);

                //System.out.println("PRE-MATCHED ORDER: " + buyQtyBefore + " with " + sellQtyBefore +
                 //       " for " + matchedQuantity + " units at $" + lowestSell.getPrice());

                // Now update the remaining quantities only after matching
                highestBuy.setQuantity(buyQtyBefore - matchedQuantity);
                lowestSell.setQuantity(sellQtyBefore - matchedQuantity);

                System.out.println("MATCHED ORDER: \n" + highestBuy + " with " + lowestSell +
                        " for " + matchedQuantity + " units at $" + lowestSell.getPrice());

                // Add matched orders to matchedOrdersList
                addMatchedOrder(highestBuy);
                addMatchedOrder(lowestSell);

                // Remove fully matched orders.
                if (highestBuy.getQuantity() <= 0) {
                    removeOrder(buyOrders, highestBuy);
                }
                if (lowestSell.getQuantity() <= 0) {
                    removeOrder(sellOrders, lowestSell);
                }
            }
        }

        private void addMatchedOrder(Order order) {
            Order oldHead;
            do {
                oldHead = matchedOrdersList.get();
                order.setNext(oldHead);
            } while (!matchedOrdersList.compareAndSet(oldHead, order));
        }

        private void removeOrder(AtomicReference<Order> head, Order orderToRemove) {
            Order current = head.get();
            // If the order to remove is at the head.
            if (current != null && orderToRemove.getId() != null && current.getId() != null && current.getId().equals(orderToRemove.getId())) {
                head.compareAndSet(current, current.getNext());
                //System.out.println("Removed Order: " + orderToRemove);
                return;
            }
            // Traverse the linked list to remove the order using a CAS loop.
            Order prev;
            current = head.get();
            while (current != null) {
                Order next = current.getNext();
                if (next != null && orderToRemove.getId() != null && next.getId() != null && next.getId().equals(orderToRemove.getId())) {
                    // Use compareAndSet on the AtomicReference that holds the next node
                    if (head.compareAndSet(current, next.getNext())) {
                       // System.out.println("Removed Order: " + orderToRemove);
                        return;
                    } else {
                        // if CAS fails, retry
                        current = head.get(); // Reset current to head and retry
                        prev = null; // Reset prev to null

                        while (current != null && (prev == null || current.getId() == null || !current.getId().equals(prev.getId()))) {
                            if (prev == null || (current.getId() != null && !current.getId().equals(prev.getId()))) {
                                prev = current;
                            }
                            current = current.getNext();
                        }
                    }
                } else {
                    current = next;
                }
            }
        }

        public Order[] getAllOrders() {
            // Get all buy orders
            Order currentBuy = buyOrders.get();
            int buyOrderCount = 0;
            while (currentBuy != null) {
                buyOrderCount++;
                currentBuy = currentBuy.getNext();
            }

            // Get all sell orders
            Order currentSell = sellOrders.get();
            int sellOrderCount = 0;
            while (currentSell != null) {
                sellOrderCount++;
                currentSell = currentSell.getNext();
            }

            // Create an array to hold all orders
            Order[] allOrders = new Order[buyOrderCount + sellOrderCount];

            // Add buy orders to the array
            currentBuy = buyOrders.get();
            int index = 0;
            while (currentBuy != null) {
                allOrders[index++] = currentBuy;
                currentBuy = currentBuy.getNext();
            }

            // Add sell orders to the array
            currentSell = sellOrders.get();
            while (currentSell != null) {
                allOrders[index++] = currentSell;
                currentSell = currentSell.getNext();
            }
            return allOrders;
        }

        public Order[] getMatchedOrders() {
            Order current = matchedOrdersList.get();
            int count = 0;
            while (current != null) {
                count++;
                current = current.getNext();
            }
            Order[] matchedOrders = new Order[count];
            current = matchedOrdersList.get();
            int index = 0;
            while (current != null) {
                matchedOrders[index++] = current;
                current = current.getNext();
            }
            return matchedOrders;
        }
    }
}