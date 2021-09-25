package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.model.Order;

import java.util.Comparator;

public class AskComparator implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {
         if (o1.getPrice() > o2.getPrice())
            return -1;
        else
            return 1;
    }
}
