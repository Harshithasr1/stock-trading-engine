package com.example.stock_trading_engine.controller;

import com.example.stock_trading_engine.SimulationService;
import com.example.stock_trading_engine.dto.OrderDTO;
import com.example.stock_trading_engine.model.Order;
import com.example.stock_trading_engine.service.OrderMatchingService;
import com.example.stock_trading_engine.service.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderMatchingService orderMatchingService;
    private final SimulationService simulationService;
    private final TickerService tickerService;

    @Autowired
    public OrderController(OrderMatchingService orderMatchingService,
                           SimulationService simulationService,
                           TickerService tickerService) {
        this.orderMatchingService = orderMatchingService;
        this.simulationService = simulationService;
        this.tickerService = tickerService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addOrder(@RequestBody OrderDTO orderDTO) {
        try {
            orderMatchingService.addOrder(orderDTO);
            simulationService.simulateTrading();
            return ResponseEntity.ok("Order added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add order: " + e.getMessage());
        }
    }

    @GetMapping("/book/{tickerSymbol}")
    public ResponseEntity<String> getOrderBook(@PathVariable String tickerSymbol) {
        try {
            Order[] orderBook = tickerService.getOrderBook(tickerSymbol);
            if (orderBook == null) {
                return ResponseEntity.notFound().build();
            }

            StringBuilder responseBuilder = new StringBuilder();
            for (Order order : orderBook) {
                if (order != null) {
                    responseBuilder.append(order.toString()).append("\n");
                }
            }
            return ResponseEntity.ok(responseBuilder.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve order book: " + e.getMessage());
        }
    }

    @PostMapping("/match/{tickerSymbol}")
    public ResponseEntity<String> matchOrders(@PathVariable String tickerSymbol) {
        try {
            tickerService.matchOrder(tickerSymbol);
            return ResponseEntity.ok("Orders matched successfully for " + tickerSymbol);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to match orders: " + e.getMessage());
        }
    }

    @GetMapping("/matched/{tickerSymbol}")
    public ResponseEntity<String> getMatchedOrders(@PathVariable String tickerSymbol) {
        try {
            Order[] matchedOrders = tickerService.getMatchedOrders(tickerSymbol);
            if (matchedOrders == null) {
                return ResponseEntity.notFound().build();
            }

            StringBuilder responseBuilder = new StringBuilder();
            for (Order order : matchedOrders) {
                if (order != null) {
                    responseBuilder.append(order.toString()).append("\n");
                }
            }
            return ResponseEntity.ok(responseBuilder.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve matched orders: " + e.getMessage());
        }
    }

    @GetMapping("/added")
    public ResponseEntity<String> getAllAddedOrders() {
        try {
            StringBuilder responseBuilder = new StringBuilder();
            for (int i = 0; i < 1024; i++) {
                Order[] allOrders = tickerService.getAllOrdersFromOrderBook(i);
                if (allOrders != null) {
                    for (Order order : allOrders) {
                        if (order != null) {
                            responseBuilder.append(order.toString()).append("\n");
                        }
                    }
                }
            }
            return ResponseEntity.ok(responseBuilder.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve all added orders: " + e.getMessage());
        }
    }

    @GetMapping("/allMatched")
    public ResponseEntity<String> getAllMatchedOrders() {
        try {
            StringBuilder responseBuilder = new StringBuilder();
            for (int i = 0; i < 1024; i++) {
                Order[] matchedOrders = tickerService.getAllMatchedOrdersFromOrderBook(i);
                if (matchedOrders != null) {
                    for (Order order : matchedOrders) {
                        if (order != null) {
                            responseBuilder.append(order.toString()).append("\n");
                        }
                    }
                }
            }
            return ResponseEntity.ok(responseBuilder.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve all matched orders: " + e.getMessage());
        }
    }
}