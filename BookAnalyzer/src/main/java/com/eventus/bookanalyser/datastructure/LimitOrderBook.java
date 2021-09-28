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
    private final int targetSize;
    private final Set<LimitOrderEntry> bids;
    private final Set<LimitOrderEntry> asks;
    // map for bookkeeping of order entries
    private final Map<String, LimitOrderEntry> orderBookMap;
    //private Double prevBuyExpenseTotal = 0.0;
    private double prevBuyExpenseTotal = Double.NaN;
    //private Double prevSellIncomeTotal = 0.0;
    private Double prevSellIncomeTotal = Double.NaN;
    //private Integer prevBuySizeTotal = 0;
    private int prevBuySizeTotal = -1;
    //private Integer previSellSizeTotal = 0;
    private int previSellSizeTotal = -1;

    public LimitOrderBook(String instrument, int targetSize) {
        this.instrument = instrument;
        this.targetSize = targetSize;
        this.bids = new TreeSet<>(new BidComparator());
        this.asks = new TreeSet<>(new AskComparator());
        this.orderBookMap = new HashMap<>();
    }

    @Override
    public void addOrder(LimitOrderEntry limitOrderEntry) {
        addOrderEntry(limitOrderEntry);
    }

    // method for adding order entries to bids or asks
    private void addOrderEntry(LimitOrderEntry limitOrderEntry) {
        //fail fast
        isUniqueOrder(limitOrderEntry);
        isValidAddOrder(limitOrderEntry);
        if (isBidOrder(limitOrderEntry)) {
            addOrderToList(limitOrderEntry, bids);
            calculateExpense(limitOrderEntry, targetSize);
        } else if (isAskOrder(limitOrderEntry)) {
            addOrderToList(limitOrderEntry, asks);
            calculateIncome(limitOrderEntry, targetSize);
        }

    }

    private boolean isAskOrder(LimitOrderEntry limitOrderEntry) {
        return (limitOrderEntry.getOrderType().equalsIgnoreCase(OrderTypes.A.name()) &&
                limitOrderEntry.getSide().equalsIgnoreCase(OrderTypes.S.name()));
    }

    private boolean isBidOrder(LimitOrderEntry limitOrderEntry) {
        return (limitOrderEntry.getOrderType().equalsIgnoreCase(OrderTypes.A.name()) &&
                limitOrderEntry.getSide().equalsIgnoreCase(OrderTypes.B.name()));
    }

    private void isValidAddOrder(LimitOrderEntry limitOrderEntry) {
        if (!limitOrderEntry.getOrderType().equalsIgnoreCase(OrderTypes.A.name()))
            throw new InvalidParameterException(String.format("Expected  orderType : A ; received orderType is : %s", limitOrderEntry.getOrderType()));
    }

    private void isUniqueOrder(LimitOrderEntry limitOrderEntry) {
        if (orderBookMap.get(limitOrderEntry.getOrderId()) != null)
            throw new DuplicateRequestException(String.format("Duplicate orderID %s", orderBookMap.get(limitOrderEntry.getOrderId()).toString()));
    }

    private void addOrderToList(LimitOrderEntry limitOrderEntry, Set<LimitOrderEntry> orderList) {
        orderList.add(limitOrderEntry);
        orderBookMap.put(limitOrderEntry.getOrderId(), limitOrderEntry);
    }

    @Override
    public void modifyOrder(LimitOrderEntry newOrderEntry) {
        //fail fast
        isValidRemoveOrder(newOrderEntry);

        LimitOrderEntry existingEntry = orderBookMap.get(newOrderEntry.getOrderId());
        //28800758 A d B 44.18 157
        //28800796 R d 157

        if (isExistingOrder(newOrderEntry) && isBuyOrder(newOrderEntry)) {
            if ((existingEntry.getSize() - newOrderEntry.getSize()) <= 0) {
                bids.remove(existingEntry);
                orderBookMap.remove(existingEntry.getOrderId());
            } else {
                existingEntry.setSize(existingEntry.getSize() - newOrderEntry.getSize());
                orderBookMap.put(existingEntry.getOrderId(), existingEntry);
            }
            calculateExpense(newOrderEntry, targetSize);
        } else if (isExistingOrder(newOrderEntry) && isSellOrder(newOrderEntry)) {
            if ((existingEntry.getSize() - newOrderEntry.getSize()) <= 0) {
                asks.remove(existingEntry);
                orderBookMap.remove(existingEntry.getOrderId());
            } else {
                existingEntry.setSize(existingEntry.getSize() - newOrderEntry.getSize());
                orderBookMap.put(existingEntry.getOrderId(), existingEntry);
            }
            calculateIncome(newOrderEntry, targetSize);
        }
    }

    private boolean isSellOrder(LimitOrderEntry newOrderEntry) {
        LimitOrderEntry existingEntry = orderBookMap.get(newOrderEntry.getOrderId());
        return existingEntry.getSide().equalsIgnoreCase(OrderTypes.S.name());
    }

    private boolean isBuyOrder(LimitOrderEntry newOrderEntry) {
        LimitOrderEntry existingEntry = orderBookMap.get(newOrderEntry.getOrderId());
        return existingEntry.getSide().equalsIgnoreCase(OrderTypes.B.name());
    }

    private boolean isExistingOrder(LimitOrderEntry newOrderEntry) {
        return orderBookMap.get(newOrderEntry.getOrderId()) != null;
    }

    private void isValidRemoveOrder(LimitOrderEntry newOrderEntry) {
        if (!newOrderEntry.getOrderType().equalsIgnoreCase(OrderTypes.R.name()))
            throw new InvalidParameterException(String.format("Expected orderType : R ; received orderType is : %s", newOrderEntry.getOrderType()));
    }

    // calculate the expenses
    public Double calculateExpense(LimitOrderEntry limitOrderEntry, int targetSize) {
        Double newExpenseTotal = 0.0;
        int tempTargetSize = targetSize;
        if (!bids.isEmpty()) {
            for (LimitOrderEntry orderEntry : bids) {
                if (tempTargetSize > 0 && tempTargetSize <= orderEntry.getSize()) {
                    newExpenseTotal += (tempTargetSize * orderEntry.getPrice());
                    tempTargetSize -= tempTargetSize;
                } else if (tempTargetSize > 0 && tempTargetSize > orderEntry.getSize()) {
                    newExpenseTotal += (orderEntry.getSize() * orderEntry.getPrice());
                    tempTargetSize = tempTargetSize - orderEntry.getSize();
                } else {
                    break;
                }
                //System.out.println(String.format("orderId: %s, orderSize: %d, price: %f", orderEntry.getOrderId(), orderEntry.getSize(), orderEntry.getPrice()));
                //System.out.println(String.format("tempTargetSize: %d", tempTargetSize));
                //System.out.println(String.format("Expense: %f", newExpense));
            }
        }

        if (prevBuyExpenseTotal != newExpenseTotal && (targetSize - tempTargetSize) == targetSize) {
            prevBuyExpenseTotal = newExpenseTotal;
            prevBuySizeTotal = targetSize;
            System.out.println(String.format("Output: %d %s %s", limitOrderEntry.getTimestamp(), "S", newExpenseTotal == 0 ? "NA" : String.valueOf(newExpenseTotal)));
        } else if (prevBuyExpenseTotal != newExpenseTotal && prevBuySizeTotal > tempTargetSize) {
            prevBuyExpenseTotal = Double.NaN;
            prevBuySizeTotal = -1;
            System.out.println(String.format("Output: %d %s %s", limitOrderEntry.getTimestamp(), "S", "NA"));
        }
        return newExpenseTotal;
    }

    //calculate the income
    public double calculateIncome(LimitOrderEntry limitOrderEntry, int targetSize) {
        double currentIncomeTotal = 0;
        int tempTargetSize = targetSize;
        for (LimitOrderEntry orderEntry : asks) {
            if (tempTargetSize > 0 && tempTargetSize <= orderEntry.getSize()) {
                currentIncomeTotal += (tempTargetSize * orderEntry.getPrice());
                tempTargetSize -= tempTargetSize;
            } else if (tempTargetSize > 0 && tempTargetSize > orderEntry.getSize()) {
                currentIncomeTotal += (orderEntry.getSize() * orderEntry.getPrice());
                tempTargetSize = tempTargetSize - orderEntry.getSize();
            } else {
                break;
            }
            //System.out.println(String.format("orderId: %s, orderSize: %d, price: %f", orderEntry.getOrderId(), orderEntry.getSize(), orderEntry.getPrice()));
            //System.out.println(String.format("netTargetSize: %d", tempTargetSize));
            //System.out.println(String.format("Expense: %f", newIncome));
        }

        if (prevSellIncomeTotal != currentIncomeTotal && (targetSize - tempTargetSize) == targetSize) {
            prevSellIncomeTotal = currentIncomeTotal;
            previSellSizeTotal = targetSize;
            System.out.println(String.format("Output: %d %s %s", limitOrderEntry.getTimestamp(), "B", currentIncomeTotal == 0 ? "NA" : String.valueOf(currentIncomeTotal)));
        } else if (prevSellIncomeTotal != currentIncomeTotal && previSellSizeTotal > tempTargetSize) {
            prevSellIncomeTotal = Double.NaN;
            previSellSizeTotal = -1;
            System.out.println(String.format("Output: %d %s %s", limitOrderEntry.getTimestamp(), "B", "NA"));
        }
        return currentIncomeTotal;
    }

    //utility methods are provided for better encapsulation
    public void printBidList() {
        System.out.println("Printing Bids");
        bids.forEach(x -> System.out.println(x.toString()));
    }

    public void printAskList() {
        System.out.println("Printing Asks:");
        asks.forEach(x -> System.out.println(x.toString()));
    }

    public Map<String, LimitOrderEntry> getOrderBookMap() {
        return orderBookMap;
    }

    public int getTotalBidSize() {
        return bids.size();
    }

    public int getTotalAskSize() {
        return asks.size();
    }

}
