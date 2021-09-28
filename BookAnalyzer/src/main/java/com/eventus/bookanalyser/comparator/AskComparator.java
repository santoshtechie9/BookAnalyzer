package com.eventus.bookanalyser.comparator;

import com.eventus.bookanalyser.model.LimitOrderEntry;

import java.util.Comparator;

public class AskComparator implements Comparator<LimitOrderEntry> {

    @Override
    public int compare(LimitOrderEntry o1, LimitOrderEntry o2) {
        if (o1.getPrice() > o2.getPrice())
            return 1;
        else if (o1.getPrice() < o2.getPrice())
            return -1;
        int orderIdReturn = o1.getOrderId().compareTo(o2.getOrderId());
        if (orderIdReturn != 0) {
            return orderIdReturn;
        }
        return 0;
    }

}
