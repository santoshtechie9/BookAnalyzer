package com.eventus.bookanalyser.model;

import java.util.Objects;

//use builder patten or factory pattern
public class Order  {
    /* implements Comparable<Order>*/

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

    public long getTimestamp() {
        return timestamp;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getSide() {
        return side;
    }

    public long getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "timestamp=" + timestamp +
                ", orderType='" + orderType + '\'' +
                ", orderId='" + orderId + '\'' +
                ", side='" + side + '\'' +
                ", size=" + size +
                ", price=" + price +
                '}';
    }


    // Two Orders are equal if their IDs are equal
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return this.orderId.equalsIgnoreCase(order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

/*
    // Compare employees based on their IDs
    @Override
    public int compareTo(Order order) {
        if (this.getPrice() > order.getPrice())
            return -1;
        else if (this.price == order.getPrice())
            return 0;
        else
            return 1;
    }
*/

}
