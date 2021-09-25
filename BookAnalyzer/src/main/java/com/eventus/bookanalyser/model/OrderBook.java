package com.eventus.bookanalyser.model;

import com.eventus.bookanalyser.app.Order;

//abstract data type for managing order book
public class OrderBook {

    private ListADT bidList;
    private ListADT askList;
    private double totalBidQuantity = 0;
    private double totalAskQuantity = 0;

    private void insertBuyOrder(Order order) {
    }

    private void insertSellOrder(Order order) {
    }

    private void modifyBuyOrder(Order order) {
    }

    private void modifySellOrder(Order order) {
    }

    private void delete(Order order, ListADT list) {
    }

    private void sort(ListADT orderList) {
    }

}
