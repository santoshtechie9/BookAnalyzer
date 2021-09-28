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
    private final int targerSize;
    private final Set<LimitOrderEntry> bidList;
    private final Set<LimitOrderEntry> askList;
    // map for bookkeeping of order entries
    private final Map<String, LimitOrderEntry> orderBookMap;
    private Double buyExpenseTotal = 0.0;
    private Double sellIncomeTotal = 0.0;

    public LimitOrderBook(String instrument, int targetSize) {
        this.instrument = instrument;
        this.targerSize = targetSize;
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
        //fail fast
        isUniqueOrder(limitOrderEntry);
        isValidAddOrder(limitOrderEntry);
        if (isBidOrder(limitOrderEntry)) {
            addOrderToList(limitOrderEntry, bidList);
            calculateExpense(limitOrderEntry, targerSize);
        } else if (isAskOrder(limitOrderEntry)) {
            addOrderToList(limitOrderEntry, askList);
            calculateIncome(limitOrderEntry, targerSize);
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
        if (isExistingOrder(newOrderEntry) && isBuyOrder(newOrderEntry)) {
            if ((existingEntry.getSize() - newOrderEntry.getSize()) <= 0) {
                bidList.remove(existingEntry);
                orderBookMap.remove(existingEntry.getOrderId());
            } else {
                existingEntry.setSize(existingEntry.getSize() - newOrderEntry.getSize());
                orderBookMap.put(existingEntry.getOrderId(), existingEntry);
            }
            calculateExpense(newOrderEntry, targerSize);
        } else if (isExistingOrder(newOrderEntry) && isSellOrder(newOrderEntry)) {
            if ((existingEntry.getSize() - newOrderEntry.getSize()) <= 0) {
                askList.remove(existingEntry);
                orderBookMap.remove(existingEntry.getOrderId());
            } else {
                existingEntry.setSize(existingEntry.getSize() - newOrderEntry.getSize());
                orderBookMap.put(existingEntry.getOrderId(), existingEntry);
            }
            calculateIncome(newOrderEntry, targerSize);
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
        if (!bidList.isEmpty()) {
            for (LimitOrderEntry orderEntry : bidList) {
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

        if (buyExpenseTotal != newExpenseTotal && (targetSize - tempTargetSize) == targetSize) {
            buyExpenseTotal = newExpenseTotal;
            System.out.println(String.format("Output: %d %s %s", limitOrderEntry.getTimestamp(), "S", newExpenseTotal == 0 ? "NA" : String.valueOf(newExpenseTotal)));
        }
        return newExpenseTotal;
    }

    //calculate the income
    public double calculateIncome(LimitOrderEntry limitOrderEntry, int targetSize) {
        double currentIncomeTotal = 0;
        int tempTargetSize = targetSize;
        for (LimitOrderEntry orderEntry : askList) {
            if (tempTargetSize > 0 && tempTargetSize <= orderEntry.getSize()) {
                currentIncomeTotal += (tempTargetSize * orderEntry.getPrice());
                tempTargetSize -= tempTargetSize;
            } else if (tempTargetSize > 0 && tempTargetSize > orderEntry.getSize()) {
                currentIncomeTotal += (orderEntry.getSize() * orderEntry.getPrice());
                tempTargetSize -= orderEntry.getSize();
            } else {
                break;
            }
            //System.out.println(String.format("orderId: %s, orderSize: %d, price: %f", orderEntry.getOrderId(), orderEntry.getSize(), orderEntry.getPrice()));
            //System.out.println(String.format("netTargetSize: %d", tempTargetSize));
            //System.out.println(String.format("Expense: %f", newIncome));
        }

        if (sellIncomeTotal != currentIncomeTotal && (targetSize - tempTargetSize) == targetSize) {
            buyExpenseTotal = currentIncomeTotal;
            System.out.println(String.format("Output: %d %s %s", limitOrderEntry.getTimestamp(), "B", currentIncomeTotal == 0 ? "NA" : String.valueOf(currentIncomeTotal)));
        }
        return currentIncomeTotal;
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

}
