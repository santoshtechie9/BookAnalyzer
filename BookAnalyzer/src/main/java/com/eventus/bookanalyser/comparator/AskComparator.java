package com.eventus.bookanalyser.comparator;

import com.eventus.bookanalyser.model.LimitOrderEntry;

import java.util.Comparator;

public class AskComparator implements Comparator<LimitOrderEntry> {

    @Override
    public int compare(LimitOrderEntry o1, LimitOrderEntry o2) {
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
