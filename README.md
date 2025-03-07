# Real-time Stock Trading Engine

## Overview
This Spring Boot application simulates a real-time stock trading engine that supports adding buy and sell orders for 1,024 tickers, matches orders using a lock-free data structure, and provides REST APIs for order management.

## Prerequisites
- Java 17 or higher
- Maven
- Spring Boot

## Execution Steps

1.  Clone the repository.
2.  Navigate to the project directory.
3.  Build the project using Maven.
  use: mvn clean install 

## Run the Application

The application will start on port 8080.
The Simulated orders and Matched orders will be displayed on the console.

To check dynamically using API Endpoints:
Use Postman to add Orders: 
POST: http://localhost:8080/api/orders/add
{
    "orderType": "SELL",
    "tickerSymbol": "AMZN",
    "quantity": "14",
    "price": "220"
}

To check matched orders: 
GET: http://localhost:8080/api/orders/added

To check added orders: 
GET: http://localhost:8080/api/orders/allMatched
