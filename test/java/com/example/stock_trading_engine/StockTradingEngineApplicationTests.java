package com.example.stock_trading_engine;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class StockTradingEngineApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testAddOrder() throws Exception {
		String orderJson = "{\"orderType\": \"BUY\", \"tickerSymbol\": \"AAPL\", \"quantity\": 10, \"price\": 150.0}";

		mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(orderJson))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Order added successfully."));
	}
}