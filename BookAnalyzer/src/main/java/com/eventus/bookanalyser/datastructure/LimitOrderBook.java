package com.eventus.bookanalyser.datastructure;

import com.eventus.bookanalyser.comparator.AskComparator;
import com.eventus.bookanalyser.comparator.BidComparator;
import com.eventus.bookanalyser.model.LimitOrderEntry;
import com.eventus.bookanalyser.model.OrderBookNotificationEvent;
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
            calculateUpdatedExpense(limitOrderEntry, targetSize);
        } else if (isAskOrder(limitOrderEntry)) {
            addOrderToList(limitOrderEntry, asks);
            calculateUpdatedIncome(limitOrderEntry, targetSize);
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
            calculateUpdatedExpense(newOrderEntry, targetSize);
        } else if (isExistingOrder(newOrderEntry) && isSellOrder(newOrderEntry)) {
            if ((existingEntry.getSize() - newOrderEntry.getSize()) <= 0) {
                asks.remove(existingEntry);
                orderBookMap.remove(existingEntry.getOrderId());
            } else {
                existingEntry.setSize(existingEntry.getSize() - newOrderEntry.getSize());
                existingEntry.setTimestamp(newOrderEntry.getTimestamp());
                orderBookMap.put(existingEntry.getOrderId(), existingEntry);
            }
            calculateUpdatedIncome(newOrderEntry, targetSize);
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

    public double calculateUpdatedExpense(LimitOrderEntry currOrderEntry, int targetSize) {
        int bidInstrCount = getTotalInstrumentCount(bids);
        double currExpense = 0.0;
        int currBuySize = 0;
        if (bidInstrCount >= targetSize) {
            for (LimitOrderEntry bidEntry : bids) {
                if ((targetSize - currBuySize) >= bidEntry.getSize()) {
                    currExpense += (bidEntry.getSize() * bidEntry.getPrice());
                    currBuySize += bidEntry.getSize();
                } else if ((targetSize - currBuySize) < bidEntry.getSize() && (targetSize - currBuySize) > 0) {
                    currExpense += (targetSize - currBuySize) * bidEntry.getPrice();
                    currBuySize += (targetSize - currBuySize);
                } else if ((targetSize - currBuySize) == 0) {
                    break;
                }
            }
        }
        currExpense = Double.valueOf(df_obj.format(currExpense));
        if (bidInstrCount >= targetSize && prevExpense != currExpense) {
            prevExpense = currExpense;
            prevBuySize = targetSize;
            updateEventStatus(currOrderEntry.getTimestamp(), OrderTypes.S.name(), String.format("%.2f", currExpense));
        } else if (bidInstrCount <= targetSize && prevExpense != currExpense && prevBuySize > currBuySize) {
            prevExpense = Double.NaN;
            prevBuySize = -1;
            updateEventStatus(currOrderEntry.getTimestamp(), OrderTypes.S.name(), "NA");
        }
        return currExpense;
    }

    public double calculateUpdatedIncome(LimitOrderEntry currOrderEntry, int targetSize) {
        int askInstrumentsSize = getTotalInstrumentCount(asks);
        double currIncome = 0.0;
        int currSellSize = 0;
        if (askInstrumentsSize >= targetSize) {
            for (LimitOrderEntry askEntry : asks) {
                if ((targetSize - currSellSize) >= askEntry.getSize()) {
                    currIncome += (askEntry.getSize() * askEntry.getPrice());
                    currSellSize += askEntry.getSize();
                } else if ((targetSize - currSellSize) < askEntry.getSize() && (targetSize - currSellSize) > 0) {
                    currIncome += (targetSize - currSellSize) * askEntry.getPrice();
                    currSellSize += (targetSize - currSellSize);
                } else if ((targetSize - currSellSize) == 0) {
                    break;
                }
            }
        }
        currIncome = Double.valueOf(df_obj.format(currIncome));
        if (askInstrumentsSize >= targetSize && prevIncome != currIncome) {
            prevIncome = currIncome;
            prevSellSize = targetSize;
            updateEventStatus(currOrderEntry.getTimestamp(), OrderTypes.B.name(), String.format("%.2f", currIncome));
        } else if (askInstrumentsSize <= targetSize && prevIncome != currIncome && prevSellSize > currSellSize) {
            prevIncome = Double.NaN;
            prevSellSize = -1;
            updateEventStatus(currOrderEntry.getTimestamp(), OrderTypes.B.name(), "NA");
        }
        return currIncome;
    }

    private void updateEventStatus(Long timestamp, String side, String total) {
        OrderBookNotificationEvent notifyEvent = new OrderBookNotificationEvent();
        notifyEvent.setTimestamp(timestamp);
        notifyEvent.setSide(side);
        notifyEvent.setTotal(total);
        setChanged();
        notifyObservers(notifyEvent);
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
