package com.eventus.bookanalyser.model;

//use builder patten or factory pattern
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

    public boolean equals(Object obj) {
        if (obj instanceof Order) {
            Order o = (Order) obj;
            return o.orderId == this.orderId;
        }
        return false;
    }
}
