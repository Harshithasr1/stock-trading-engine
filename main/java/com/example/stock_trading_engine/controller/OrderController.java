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
            simulationService.simulateTrading();
            orderMatchingService.addOrder(orderDTO);
            return ResponseEntity.ok("Order added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add order: " + e.getMessage());
        }
    }

    // Endpoint to get all active or added orders for a ticker
    @GetMapping("/all/{tickerSymbol}")
    public ResponseEntity<String> getAllOrders(@PathVariable String tickerSymbol) {
        try {
            Order[] orders = tickerService.getOrderBook(tickerSymbol);
            if (orders == null) {
                return ResponseEntity.notFound().build();
            }
            StringBuilder responseBuilder = new StringBuilder();
            for (Order order : orders) {
                if (order != null) {
                    responseBuilder.append(order.toString()).append("\n");
                }
            }
            return ResponseEntity.ok(responseBuilder.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve all orders: " + e.getMessage());
        }
    }

    // Endpoint to get matched orders for a ticker
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve matched orders: " + e.getMessage());
        }
    }

    @PostMapping("/match/{tickerSymbol}")
    public ResponseEntity<String> matchOrders(@PathVariable String tickerSymbol) {
        try {
            tickerService.matchOrder(tickerSymbol);
            return ResponseEntity.ok("Orders matched successfully for " + tickerSymbol);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to match orders: " + e.getMessage());
        }
    }
}