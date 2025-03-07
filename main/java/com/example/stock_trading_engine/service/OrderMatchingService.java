package com.example.stock_trading_engine.service;

import com.example.stock_trading_engine.dto.OrderDTO;
import com.example.stock_trading_engine.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderMatchingService {

    private final TickerService tickerService;

    @Autowired
    public OrderMatchingService(TickerService tickerService) {
        this.tickerService = tickerService;
    }

    public void addOrder(OrderDTO orderDTO) {
        Order order = new Order(orderDTO.getOrderType(), orderDTO.getTickerSymbol(), orderDTO.getQuantity(), orderDTO.getPrice());
        tickerService.addOrder(order);
    }
}