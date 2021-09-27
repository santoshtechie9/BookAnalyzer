package com.eventus.bookanalyser.datastructure;

import com.eventus.bookanalyser.comparator.AskComparator;
import com.eventus.bookanalyser.comparator.BidComparator;
import com.eventus.bookanalyser.model.LimitOrderEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class LimitOrderBook implements IOrderBook {

    // instrument id the order book belongs to.
    private final String instrument;
    private final Set<LimitOrderEntry> bidList;
    private final Set<LimitOrderEntry> askList;
    // map for bookkeeping of order entries
    private final Map<String, LimitOrderEntry> orderBookMap;

    public LimitOrderBook(String instrument) {
        this.instrument = instrument;
        this.bidList = new TreeSet<>(new BidComparator());
        this.askList = new TreeSet<>(new AskComparator());
        this.orderBookMap = new HashMap<>();
    }

    @Override
    public void addOrder(LimitOrderEntry limitOrderEntry) {
        addOrderEntry(limitOrderEntry);
    }

    // method for adding order entries to bids or asks
    private void addOrderEntry(LimitOrderEntry limitOrderEntry) {
        //validate limitOrderEntry
        if (limitOrderEntry.getOrderType().equalsIgnoreCase("A") &&
                limitOrderEntry.getSide().equalsIgnoreCase("B")) {
            addOrderToList(limitOrderEntry, bidList);
        } else if (limitOrderEntry.getOrderType().equalsIgnoreCase("A") &&
                limitOrderEntry.getSide().equalsIgnoreCase("S")) {
            addOrderToList(limitOrderEntry, askList);
        }
    }

    private void addOrderToList(LimitOrderEntry limitOrderEntry, Set<LimitOrderEntry> orderList) {
        orderList.add(limitOrderEntry);
        orderBookMap.put(limitOrderEntry.getOrderId(), limitOrderEntry);
    }

    @Override
    public void modifyOrder(LimitOrderEntry limitOrderEntry) {
        if (limitOrderEntry.getOrderType().equalsIgnoreCase("R") &&
                limitOrderEntry.getSide().equalsIgnoreCase("B")) {
            modifyOrderEntry(limitOrderEntry, bidList, orderBookMap);
        } else if (limitOrderEntry.getOrderType().equalsIgnoreCase("R") &&
                limitOrderEntry.getSide().equalsIgnoreCase("S")) {
            System.out.println("inside asks");
            modifyOrderEntry(limitOrderEntry, askList, orderBookMap);
        }
    }

    // Update or delete order entry from bidList or askList
    private void modifyOrderEntry(LimitOrderEntry limitOrderEntry, Set<LimitOrderEntry> orderList,
                                  Map<String, LimitOrderEntry> orderBookMap) {
        LimitOrderEntry existingEntry = orderBookMap.get(limitOrderEntry.getOrderId());
        if (limitOrderEntry.getSize() == 0) {
            System.out.println(String.format("remove orderID: %s, size: %d", limitOrderEntry.getOrderId(), limitOrderEntry.getSize()));
            if (!orderList.isEmpty() && existingEntry != null) {
                System.out.println("Existing Order : " + existingEntry);
                System.out.println("New Order : " + limitOrderEntry);
                orderList.remove(existingEntry);
                orderBookMap.remove(existingEntry.getOrderId());
            } else {
                throw new IllegalStateException("Order not found in the OrderBook!");
            }
        } else {
            System.out.println(String.format("modify orderId %s, size: %d", limitOrderEntry.getOrderId(), limitOrderEntry.getSize()));
            if (!orderList.isEmpty() && existingEntry != null) {
                orderList.remove(existingEntry);
                orderBookMap.remove(existingEntry.getOrderId());
                orderList.add(limitOrderEntry);
                orderBookMap.put(limitOrderEntry.getOrderId(), limitOrderEntry);
            } else {
                throw new IllegalStateException("Order not found in the OrderBook!");
            }
        }
    }

    // calculate the expenses
    public double calculateExpense(int targetSize) {
        double newExpense = 0;
        int tempTargetSize = targetSize;
        for (LimitOrderEntry orderEntry : bidList) {
            if (tempTargetSize > 0 && tempTargetSize <= orderEntry.getSize()) {
                newExpense += (tempTargetSize * orderEntry.getPrice());
                tempTargetSize -= tempTargetSize;
            } else if (tempTargetSize > 0 && tempTargetSize > orderEntry.getSize()) {
                newExpense += (orderEntry.getSize() * orderEntry.getPrice());
                tempTargetSize -= orderEntry.getSize();
            } else {
                break;
            }
            System.out.println(String.format("orderId: %s, orderSize: %d, price: %f", orderEntry.getOrderId(), orderEntry.getSize(), orderEntry.getPrice()));
            System.out.println(String.format("netTargetSize: %d", tempTargetSize));
            System.out.println(String.format("Expense: %f", newExpense));
        }
        return newExpense;
    }

    //calculate the income
    public double calculateIncome(int targetSize) {
        double newIncome = 0;
        int tempTargetSize = targetSize;
        for (LimitOrderEntry orderEntry : askList) {
            if (tempTargetSize > 0 && tempTargetSize <= orderEntry.getSize()) {
                newIncome += (tempTargetSize * orderEntry.getPrice());
                tempTargetSize -= tempTargetSize;
            } else if (tempTargetSize > 0 && tempTargetSize > orderEntry.getSize()) {
                newIncome += (orderEntry.getSize() * orderEntry.getPrice());
                tempTargetSize -= orderEntry.getSize();
            } else {
                break;
            }
            System.out.println(String.format("orderId: %s, orderSize: %d, price: %f", orderEntry.getOrderId(), orderEntry.getSize(), orderEntry.getPrice()));
            System.out.println(String.format("netTargetSize: %d", tempTargetSize));
            System.out.println(String.format("Expense: %f", newIncome));
        }
        return newIncome;
    }

    public String getInstrument() {
        return instrument;
    }

    public Set<LimitOrderEntry> getBidList() {
        return bidList;
    }

    public Set<LimitOrderEntry> getAskList() {
        return askList;
    }

    public Map<String, LimitOrderEntry> getOrderBookMap() {
        return orderBookMap;
    }

}
