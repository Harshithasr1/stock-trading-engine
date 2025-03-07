package com.example.stock_trading_engine;

import com.example.stock_trading_engine.dto.OrderDTO;
import com.example.stock_trading_engine.enums.OrderType;
import com.example.stock_trading_engine.service.OrderMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SimulationService {

    private static final String[] TICKERS = {"AAPL", "GOOG", "MSFT", "AMZN", "TSLA"};
    private final Random random = new Random();
    private final OrderMatchingService orderMatchingService;

    @Autowired
    public SimulationService(OrderMatchingService orderMatchingService) {
        this.orderMatchingService = orderMatchingService;
    }

    @Scheduled(fixedRate = 1000) // Run every 1 second
    public void simulateTrading() {
        OrderDTO orderDTO = generateRandomOrder();
        orderMatchingService.addOrder(orderDTO);
        System.out.println("Simulated Order: " + orderDTO.getTickerSymbol() + ", " +
                                                 orderDTO.getOrderType().toString() + ", Price: $" +
                                                 orderDTO.getPrice() + ", Quantity: " +
                                                 orderDTO.getQuantity());
    }

    private OrderDTO generateRandomOrder() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderType(random.nextBoolean() ? OrderType.BUY : OrderType.SELL);
        orderDTO.setTickerSymbol(TICKERS[random.nextInt(TICKERS.length)]);
        orderDTO.setQuantity(random.nextInt(100) + 1);
        orderDTO.setPrice(Math.round((random.nextDouble() * 1000 + 10) * 100.0) / 100.0);
        return orderDTO;
    }

}