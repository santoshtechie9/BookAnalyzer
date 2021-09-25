package com.eventus.bookanalyser.app;

public class Order {

    private final long timestamp;
    private final String orderType;
    private final String orderId;
    private final String side;
    private final long size;
    private double price;


    public Order(long timestamp, String orderType, String orderId, String side, long size) {
        this(timestamp, orderType, orderId, side, size, 0);
    }

    public Order(long timestamp, String orderType, String orderId, String side, long size, double price) {
        this.timestamp = timestamp;
        this.orderType = orderType;
        this.orderId = orderId;
        this.side = side;
        this.price = price;
        this.size = size;
    }

}
