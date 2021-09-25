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

    private void insertBuyOrder(Order order) {
        bidList.add(order);
    }

    private void insertSellOrder(Order order) {
        askList.add(order);
    }

    private void modifyBuyOrder(Order order) {
        if (order.getSize() > 0)
            bidList.add(order);
        else if (order.getSize() == 0)
            bidList.remove(order);
        else
            throw new IllegalArgumentException();
    }


    private void modifySellOrder(Order order) {
        if (order.getSize() > 0)
            askList.add(order);
        else if (order.getSize() == 0)
            askList.remove(order);
        else
            throw new IllegalArgumentException();
    }


    public Set<Order> getBidList() {
        return bidList;
    }

    public void setBidList(Set<Order> bidList) {
        this.bidList = bidList;
    }

    public Set<Order> getAskList() {
        return askList;
    }

    public void setAskList(Set<Order> askList) {
        this.askList = askList;
    }

    public double getTotalBidQuantity() {
        return totalBidQuantity;
    }

    public void setTotalBidQuantity(double totalBidQuantity) {
        this.totalBidQuantity = totalBidQuantity;
    }

    public double getTotalAskQuantity() {
        return totalAskQuantity;
    }

    public void setTotalAskQuantity(double totalAskQuantity) {
        this.totalAskQuantity = totalAskQuantity;
    }
}


