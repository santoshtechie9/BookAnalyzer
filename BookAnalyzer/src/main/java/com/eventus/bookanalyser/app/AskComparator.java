package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.model.Order;

import java.util.Comparator;

public class AskComparator implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {
        if (o1.getPrice() > o2.getPrice())
            return -1;
        else if (o1.getPrice() < o2.getPrice())
            return 1;
        int orderIdReturn = o1.getOrderId().compareTo(o2.getOrderId());
        if (orderIdReturn != 0) {
            //int tsReturn = Long.valueOf(o1.getTimestamp()).compareTo(o2.getTimestamp());
            //return orderIdReturn & tsReturn;
            return orderIdReturn;
        }
        return 0;
    }

}
