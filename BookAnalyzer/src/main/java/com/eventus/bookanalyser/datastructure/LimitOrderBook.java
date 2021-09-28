package com.eventus.bookanalyser.datastructure;

import com.eventus.bookanalyser.comparator.AskComparator;
import com.eventus.bookanalyser.comparator.BidComparator;
import com.eventus.bookanalyser.model.LimitOrderEntry;
import com.eventus.bookanalyser.model.NotifyOrderBookEvent;
import com.sun.jdi.request.DuplicateRequestException;

import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.*;

public class LimitOrderBook extends Observable implements IOrderBook {

    // instrument id to which the order book belongs to.
    private final String instrument;
    private final int targetSize;
    private final Set<LimitOrderEntry> bids;
    private final Set<LimitOrderEntry> asks;
    // map for bookkeeping of order entries
    private final Map<String, LimitOrderEntry> orderBookMap;
    private double prevExpense = 0.0;
    private double prevIncome = 0.0;
    private int prevBuySize = -1;
    private int prevSellSize = -1;
    /// create an object of DecimalFormat class
    private static final DecimalFormat df_obj = new DecimalFormat("#.##");

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
            calculateExpenseNew(limitOrderEntry, targetSize);
        } else if (isAskOrder(limitOrderEntry)) {
            addOrderToList(limitOrderEntry, asks);
            calculateIncomeNew(limitOrderEntry, targetSize);
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
                bids.remove(existingEntry);
                orderBookMap.remove(existingEntry.getOrderId());
            } else {
                existingEntry.setSize(existingEntry.getSize() - newOrderEntry.getSize());
                orderBookMap.put(existingEntry.getOrderId(), existingEntry);
            }
            calculateExpenseNew(newOrderEntry, targetSize);
        } else if (isExistingOrder(newOrderEntry) && isSellOrder(newOrderEntry)) {
            if ((existingEntry.getSize() - newOrderEntry.getSize()) <= 0) {
                asks.remove(existingEntry);
                orderBookMap.remove(existingEntry.getOrderId());
            } else {
                existingEntry.setSize(existingEntry.getSize() - newOrderEntry.getSize());
                existingEntry.setTimestamp(newOrderEntry.getTimestamp());
                orderBookMap.put(existingEntry.getOrderId(), existingEntry);
            }
            calculateIncomeNew(newOrderEntry, targetSize);
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


    private int getTotalInstrumentCount(Set<LimitOrderEntry> set) {
        int tmpCounter = 0;
        if (!set.isEmpty()) {
            for (LimitOrderEntry limitOrderEntry : set) {
                tmpCounter += limitOrderEntry.getSize();
                if (tmpCounter >= targetSize)
                    break;
            }
            return tmpCounter;
        } else
            return tmpCounter;
    }

    public double calculateExpenseNew(LimitOrderEntry currOrderEntry, int targetSize) {
        int bidInstrCount = getTotalInstrumentCount(bids);
        double tmpCurrExpense = 0.0;
        int tmpTargetSize = 0;
        if (bidInstrCount >= targetSize) {
            for (LimitOrderEntry bidEntry : bids) {
                if ((targetSize - tmpTargetSize) >= bidEntry.getSize()) {
                    tmpCurrExpense += (bidEntry.getSize() * bidEntry.getPrice());
                    tmpTargetSize += bidEntry.getSize();
                } else if ((targetSize - tmpTargetSize) < bidEntry.getSize() && (targetSize - tmpTargetSize) > 0) {
                    tmpCurrExpense += (targetSize - tmpTargetSize) * bidEntry.getPrice();
                    tmpTargetSize += (targetSize - tmpTargetSize);
                } else if ((targetSize - tmpTargetSize) == 0) {
                    break;
                }
            }
        }
        //print output
        tmpCurrExpense = Double.valueOf(df_obj.format(tmpCurrExpense));
        if (bidInstrCount >= targetSize && prevExpense != tmpCurrExpense) {
            prevExpense = tmpCurrExpense;
            prevBuySize = targetSize;
            //System.out.println(String.format("%d %s %.2f", currOrderEntry.getTimestamp(), "S", tmpCurrExpense));
            updateEventStatus(currOrderEntry.getTimestamp(), OrderTypes.S.name(), String.format("%.2f", tmpCurrExpense));
        } else if (bidInstrCount <= targetSize && prevExpense != tmpCurrExpense && prevBuySize > tmpTargetSize) {
            prevExpense = Double.NaN;
            prevBuySize = -1;
            //System.out.println(String.format("%d %s %s", currOrderEntry.getTimestamp(), "S", "NA"));
            updateEventStatus(currOrderEntry.getTimestamp(), OrderTypes.S.name(), "NA");
        }
        return tmpCurrExpense;
    }

    private void updateEventStatus(Long timestamp, String side, String total) {
        NotifyOrderBookEvent notifyEvent = new NotifyOrderBookEvent();
        notifyEvent.setTimestamp(timestamp);
        notifyEvent.setSide(side);
        notifyEvent.setTotal(total);
        setChanged();
        notifyObservers(notifyEvent);
    }

    public double calculateIncomeNew(LimitOrderEntry currOrderEntry, int targetSize) {
        int askInstrumentsSize = getTotalInstrumentCount(asks);
        double tmpCurrIncome = 0.0;
        int tmpTargetSize = 0;
        if (askInstrumentsSize >= targetSize) {
            for (LimitOrderEntry askEntry : asks) {
                if ((targetSize - tmpTargetSize) >= askEntry.getSize()) {
                    tmpCurrIncome += (askEntry.getSize() * askEntry.getPrice());
                    tmpTargetSize += askEntry.getSize();
                } else if ((targetSize - tmpTargetSize) < askEntry.getSize() && (targetSize - tmpTargetSize) > 0) {
                    tmpCurrIncome += (targetSize - tmpTargetSize) * askEntry.getPrice();
                    tmpTargetSize += (targetSize - tmpTargetSize);
                } else if ((targetSize - tmpTargetSize) == 0) {
                    break;
                }
            }
        }
        //print output
        tmpCurrIncome = Double.valueOf(df_obj.format(tmpCurrIncome));
        if (askInstrumentsSize >= targetSize && prevIncome != tmpCurrIncome) {
            prevIncome = tmpCurrIncome;
            prevSellSize = targetSize;
            //System.out.println(String.format("%d %s %.2f", currOrderEntry.getTimestamp(), "B", tmpCurrIncome));
            updateEventStatus(currOrderEntry.getTimestamp(), OrderTypes.B.name(), String.format("%.2f", tmpCurrIncome));
        } else if (askInstrumentsSize <= targetSize && prevIncome != tmpCurrIncome && prevSellSize > tmpTargetSize) {
            prevIncome = Double.NaN;
            prevSellSize = -1;
            //System.out.println(String.format("%d %s %s", currOrderEntry.getTimestamp(), "B", "NA"));
            updateEventStatus(currOrderEntry.getTimestamp(), OrderTypes.B.name(), "NA");
        }
        return tmpCurrIncome;
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

    public int getTotalBidSize() {
        return bids.size();
    }

    public int getTotalAskSize() {
        return asks.size();
    }

}
