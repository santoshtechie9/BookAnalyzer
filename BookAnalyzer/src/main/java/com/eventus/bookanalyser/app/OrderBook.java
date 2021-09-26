package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.model.Order;

import java.util.Set;
import java.util.TreeSet;

//data structure for managing the order book
public class OrderBook {

    private Set<Order> bidList = new TreeSet(new BidComparator());
    private Set<Order> askList = new TreeSet(new AskComparator());
    private double totalBidQuantity = 0;
    private double totalAskQuantity = 0;

    public void insertBuyOrder(Order order) {
        bidList.add(order);
        this.totalBidQuantity += order.getSize();
    }

    public void insertSellOrder(Order order) {
        askList.add(order);
    }

    public void modifyBuyOrder(Order order) {
        if (order.getSize() > 0) {
            bidList.add(order);
            this.totalBidQuantity += order.getSize();
        }
        else if (order.getSize() == 0)
            bidList.remove(order);
        else
            throw new IllegalArgumentException();
    }


    public void modifySellOrder(Order order) {
        if (order.getSize() > 0)
            askList.add(order);
        else if (order.getSize() == 0)
            askList.remove(order);
        else
            throw new IllegalArgumentException();
    }

    private double spread()
    {
        return 0.0;
    }

    public double getTotalBidQuantity() {
        return totalBidQuantity;
    }

    public double getTotalAskQuantity() {
        return totalAskQuantity;
    }

}


