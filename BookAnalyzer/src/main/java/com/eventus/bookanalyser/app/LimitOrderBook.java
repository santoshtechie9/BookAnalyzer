package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.comparator.AskComparator;
import com.eventus.bookanalyser.comparator.BidComparator;
import com.eventus.bookanalyser.model.LimitOrderEntry;

import java.util.*;

public class LimitOrderBook implements IOrderBook {

    private final String instrument;
    private final Set<LimitOrderEntry> bidList;
    private final Set<LimitOrderEntry> askList;
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

    private void addOrderEntry(LimitOrderEntry limitOrderEntry) {
        //validate limitOrderEntry
        if (limitOrderEntry.getOrderType().equalsIgnoreCase("A") &&
                limitOrderEntry.getSide().equalsIgnoreCase("B")) {
            addOrder(limitOrderEntry, bidList);
        } else if (limitOrderEntry.getOrderType().equalsIgnoreCase("A") &&
                limitOrderEntry.getSide().equalsIgnoreCase("S")) {
            addOrder(limitOrderEntry, askList);
        }
    }

    private void addOrder(LimitOrderEntry limitOrderEntry, Set<LimitOrderEntry> orderList) {
        orderList.add(limitOrderEntry);
        orderBookMap.put(limitOrderEntry.getOrderId(), limitOrderEntry);
    }

    @Override
    public void modifyOrder(LimitOrderEntry limitOrderEntry) {
        if (limitOrderEntry.getOrderType().equalsIgnoreCase("R") &&
                limitOrderEntry.getSide().equalsIgnoreCase("B")) {
            modifyOrder(limitOrderEntry, bidList, orderBookMap);
        } else if (limitOrderEntry.getOrderType().equalsIgnoreCase("R") &&
                limitOrderEntry.getSide().equalsIgnoreCase("S")) {
            System.out.println("inside asks");
            modifyOrder(limitOrderEntry, askList, orderBookMap);
        }
    }

    private void modifyOrder(LimitOrderEntry limitOrderEntry, Set<LimitOrderEntry> orderList,
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

    public double calculateExpense(int targetSize) {
        double newExpense = 0;
        int netTargetSize = targetSize;
        for (LimitOrderEntry orderEntry : bidList) {
            if (netTargetSize > 0 && netTargetSize <= orderEntry.getSize()) {
                newExpense += (netTargetSize * orderEntry.getPrice());
                netTargetSize -= netTargetSize;
            } else if (netTargetSize > 0 && netTargetSize > orderEntry.getSize()) {
                newExpense += (orderEntry.getSize() * orderEntry.getPrice());
                netTargetSize -= orderEntry.getSize();
            } else {
                break;
            }
            System.out.println(String.format("orderId: %s, orderSize: %d, price: %f", orderEntry.getOrderId(), orderEntry.getSize(), orderEntry.getPrice()));
            System.out.println(String.format("netTargetSize: %d", netTargetSize));
            System.out.println(String.format("Expense: %f", newExpense));
        }
        return newExpense;
    }

    public double calculateIncome(int targetSize) {
        double newIncome = 0;
        int netTargetSize = targetSize;
        for (LimitOrderEntry orderEntry : askList) {
            if (netTargetSize > 0 && netTargetSize <= orderEntry.getSize()) {
                newIncome += (netTargetSize * orderEntry.getPrice());
                netTargetSize -= netTargetSize;
            } else if (netTargetSize > 0 && netTargetSize > orderEntry.getSize()) {
                newIncome += (orderEntry.getSize() * orderEntry.getPrice());
                netTargetSize -= orderEntry.getSize();
            } else {
                break;
            }
            System.out.println(String.format("orderId: %s, orderSize: %d, price: %f", orderEntry.getOrderId(), orderEntry.getSize(), orderEntry.getPrice()));
            System.out.println(String.format("netTargetSize: %d", netTargetSize));
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
