package com.example.stock_trading_engine.controller;

import com.example.stock_trading_engine.SimulationService;
import com.example.stock_trading_engine.dto.OrderDTO;
import com.example.stock_trading_engine.service.OrderMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderMatchingService orderMatchingService;

    @Autowired
    private SimulationService simulationService;

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
}