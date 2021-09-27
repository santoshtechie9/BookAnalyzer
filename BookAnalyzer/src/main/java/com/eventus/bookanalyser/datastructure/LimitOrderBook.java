package com.eventus.bookanalyser.datastructure;

import com.eventus.bookanalyser.comparator.AskComparator;
import com.eventus.bookanalyser.comparator.BidComparator;
import com.eventus.bookanalyser.model.LimitOrderEntry;
import com.sun.jdi.request.DuplicateRequestException;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class LimitOrderBook implements IOrderBook {

    // instrument id to which the order book belongs to.
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
        //fail fast checks
        if (orderBookMap.get(limitOrderEntry.getOrderId()) != null)
            throw new DuplicateRequestException(String.format("Duplicate orderID %s", orderBookMap.get(limitOrderEntry.getOrderId()).toString()));
        if (!limitOrderEntry.getOrderType().equalsIgnoreCase("A"))
            throw new InvalidParameterException(String.format("Expected  orderType : A ; received orderType is : %s", limitOrderEntry.getOrderType()));

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
    public void modifyOrder(LimitOrderEntry newOrderEntry) {
        //fail fast checks
        if (!newOrderEntry.getOrderType().equalsIgnoreCase("R"))
            throw new InvalidParameterException(String.format("Expected orderType : R ; received orderType is : %s", newOrderEntry.getOrderType()));

        LimitOrderEntry existingEntry = orderBookMap.get(newOrderEntry.getOrderId());
        if (existingEntry != null && existingEntry.getSide().equalsIgnoreCase("B")) {
            if ((existingEntry.getSize() - newOrderEntry.getSize()) <= 0) {
                bidList.remove(existingEntry);
                orderBookMap.remove(existingEntry.getOrderId());
            } else {
                existingEntry.setSize(existingEntry.getSize() - newOrderEntry.getSize());
                //bidList.remove(existingEntry);
                //bidList.add(existingEntry);
                orderBookMap.put(existingEntry.getOrderId(), existingEntry);
            }
        } else if (existingEntry != null && existingEntry.getSide().equalsIgnoreCase("S")) {
            if ((existingEntry.getSize() - newOrderEntry.getSize()) <= 0) {
                askList.remove(existingEntry);
                orderBookMap.remove(existingEntry.getOrderId());
            } else {
                existingEntry.setSize(existingEntry.getSize() - newOrderEntry.getSize());
                //askList.remove(existingEntry);
                //askList.add(existingEntry);
                orderBookMap.put(existingEntry.getOrderId(), existingEntry);
            }
        }
    }

    // calculate the expenses
    public double calculateExpense(int targetSize) {
        double newExpense = 0;
        int tempTargetSize = targetSize;
        if (!bidList.isEmpty()) {
            for (LimitOrderEntry orderEntry : bidList) {
                if (tempTargetSize > 0 && tempTargetSize <= orderEntry.getSize()) {
                    newExpense += (tempTargetSize * orderEntry.getPrice());
                    tempTargetSize -= tempTargetSize;
                } else if (tempTargetSize > 0 && tempTargetSize > orderEntry.getSize()) {
                    newExpense += (orderEntry.getSize() * orderEntry.getPrice());
                    tempTargetSize = tempTargetSize - orderEntry.getSize();
                } else {
                    break;
                }
                System.out.println(String.format("orderId: %s, orderSize: %d, price: %f", orderEntry.getOrderId(), orderEntry.getSize(), orderEntry.getPrice()));
                System.out.println(String.format("tempTargetSize: %d", tempTargetSize));
                System.out.println(String.format("Expense: %f", newExpense));
            }
        } else
            System.out.println("No Bids submitted yet!!!");
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

    //utility methods are provided for better encapsulation
    public void printBidList() {
        System.out.println("Printing Bids");
        bidList.forEach(x -> System.out.println(x.toString()));
    }

    public void printAskList() {
        System.out.println("Printing Asks:");
        askList.forEach(x -> System.out.println(x.toString()));
    }

    public Map<String, LimitOrderEntry> getOrderBookMap() {
        return orderBookMap;
    }

    public int getTotalBidSize() {
        return bidList.size();
    }

    public int getTotalAskSize() {
        return askList.size();
    }

    public String getInstrument() {
        return instrument;
    }


}
