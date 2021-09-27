package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.comparator.AskComparator;
import com.eventus.bookanalyser.comparator.BidComparator;
import com.eventus.bookanalyser.model.LimitOrderEntry;

import java.util.Set;
import java.util.TreeSet;

//data structure for managing the order book
public class OrderBookOld {

    private Set<LimitOrderEntry> bidList = new TreeSet(new BidComparator());
    private Set<LimitOrderEntry> askList = new TreeSet(new AskComparator());
    private double totalBidQuantity = 0;
    private double totalAskQuantity = 0;

    public void insertBuyOrder(LimitOrderEntry limitOrderEntry) {
        bidList.add(limitOrderEntry);
        this.totalBidQuantity += limitOrderEntry.getSize();
    }

    public void insertSellOrder(LimitOrderEntry limitOrderEntry) {
        askList.add(limitOrderEntry);
    }

    public void modifyBuyOrder(LimitOrderEntry limitOrderEntry) {
        if (limitOrderEntry.getSize() > 0) {
            bidList.add(limitOrderEntry);
            this.totalBidQuantity += limitOrderEntry.getSize();
        } else if (limitOrderEntry.getSize() == 0)
            bidList.remove(limitOrderEntry);
        else
            throw new IllegalArgumentException();
    }


    public void modifySellOrder(LimitOrderEntry limitOrderEntry) {
        if (limitOrderEntry.getSize() > 0)
            askList.add(limitOrderEntry);
        else if (limitOrderEntry.getSize() == 0)
            askList.remove(limitOrderEntry);
        else
            throw new IllegalArgumentException();
    }

    private double spread() {
        return 0.0;
    }

    public double getTotalBidQuantity() {
        return totalBidQuantity;
    }

    public double getTotalAskQuantity() {
        return totalAskQuantity;
    }

}


