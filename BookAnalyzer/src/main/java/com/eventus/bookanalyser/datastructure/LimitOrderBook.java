package com.eventus.bookanalyser.datastructure;

import com.eventus.bookanalyser.comparator.AskComparator;
import com.eventus.bookanalyser.comparator.BidComparator;
import com.eventus.bookanalyser.model.LimitOrderEntry;
import com.sun.jdi.request.DuplicateRequestException;

import java.security.InvalidParameterException;
import java.text.DecimalFormat;
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
    private double prevBuyExpenseTotal = 0.0;
    private double prevSellIncomeTotal = 0.0;
    private int prevBuySizeTotal = -1;
    private int prevSellSizeTotal = -1;
    private int bidInstrCount = 0;
    private int askInstrCount = 0;


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
            bidInstrCount = countInstr(bids);
            calculateExpenseNew(limitOrderEntry, targetSize);
        } else if (isAskOrder(limitOrderEntry)) {
            addOrderToList(limitOrderEntry, asks);
            askInstrCount = countInstr(asks);
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

    // calculate the expenses
    public double calculateExpense(LimitOrderEntry limitOrderEntry, int targetSize) {
        double currExpenseTotal = 0.0;
        int tempTargetSize = targetSize;
        if (!bids.isEmpty()) {
            for (LimitOrderEntry orderEntry : bids) {
                if (tempTargetSize > 0 && tempTargetSize <= orderEntry.getSize()) {
                    //if (((targetSize - tempTargetSize) > 0) && tempTargetSize <= orderEntry.getSize()) {
                    currExpenseTotal += (tempTargetSize * orderEntry.getPrice());
                    tempTargetSize -= tempTargetSize;
                } else if (tempTargetSize > 0 && tempTargetSize > orderEntry.getSize()) {
                    //} else if (((targetSize - tempTargetSize) > 0) && tempTargetSize > orderEntry.getSize()) {
                    currExpenseTotal += (orderEntry.getSize() * orderEntry.getPrice());
                    tempTargetSize = tempTargetSize - orderEntry.getSize();
                } else {
                    break;
                }
            }
        }
        if ((prevBuyExpenseTotal != currExpenseTotal) && ((targetSize - tempTargetSize) == targetSize)) {
            prevBuyExpenseTotal = currExpenseTotal;
            prevBuySizeTotal = targetSize;
            System.out.println(String.format("%d %s %.2f", limitOrderEntry.getTimestamp(), "S", currExpenseTotal));
        } else if (prevBuyExpenseTotal != currExpenseTotal && prevBuySizeTotal > tempTargetSize) {
            prevBuyExpenseTotal = Double.NaN;
            prevBuySizeTotal = -1;
            System.out.println(String.format("%d %s %s", limitOrderEntry.getTimestamp(), "S", "NA"));
        }
        return currExpenseTotal;
    }

    //calculate the income
    public double calculateIncome(LimitOrderEntry limitOrderEntry, int targetSize) {
        double currIncomeTotal = 0.0;
        int tempTargetSize = targetSize;
        for (LimitOrderEntry orderEntry : asks) {
            if (tempTargetSize > 0 && tempTargetSize <= orderEntry.getSize()) {
                currIncomeTotal += (tempTargetSize * orderEntry.getPrice());
                tempTargetSize -= tempTargetSize;
            } else if (tempTargetSize > 0 && tempTargetSize > orderEntry.getSize()) {
                //} else if (((targetSize - tempTargetSize) >0) && tempTargetSize > orderEntry.getSize()) {
                currIncomeTotal += (orderEntry.getSize() * orderEntry.getPrice());
                tempTargetSize = tempTargetSize - orderEntry.getSize();
            } else {
                break;
            }
        }

        if ((prevSellIncomeTotal != currIncomeTotal) && ((targetSize - tempTargetSize) == targetSize)) {
            prevSellIncomeTotal = currIncomeTotal;
            prevSellSizeTotal = targetSize;
            System.out.println(String.format("%d %s %.2f", limitOrderEntry.getTimestamp(), "B", currIncomeTotal));
        } else if (prevSellIncomeTotal != currIncomeTotal && prevSellSizeTotal > tempTargetSize) {
            prevSellIncomeTotal = Double.NaN;
            prevSellSizeTotal = -1;
            System.out.println(String.format("%d %s %s", limitOrderEntry.getTimestamp(), "B", "NA"));
        }
        return currIncomeTotal;
    }

    private int countInstr(Set<LimitOrderEntry> set) {
        int tmpCounter = 0;
        if (set.size() != 0) {
            for (LimitOrderEntry limitOrderEntry : set) {
                tmpCounter += limitOrderEntry.getSize();
                if (tmpCounter >= targetSize)
                    break;
            }
            return tmpCounter;
        } else
            return tmpCounter;
    }

    private double calculateExpenseNew(LimitOrderEntry currOrderEntry, int targetSize) {

        int i;
        if (currOrderEntry.getTimestamp() == 28800538 || currOrderEntry.getTimestamp() ==28800744) //32913787 32913788
            i = 1;

        int bidInstrCount = countInstr(bids);
        double tmpCurrBuyExpenseTotal = 0.0;
        int tmpTargetSize = 0;
        if (bidInstrCount >= targetSize) {
            for (LimitOrderEntry bidEntry : bids) {
                if ((targetSize - tmpTargetSize) >= bidEntry.getSize()) {
                    tmpCurrBuyExpenseTotal += (bidEntry.getSize() * bidEntry.getPrice());
                    tmpTargetSize += bidEntry.getSize();
                } else if ((targetSize - tmpTargetSize) < bidEntry.getSize() && (targetSize - tmpTargetSize) > 0) {
                    tmpCurrBuyExpenseTotal += (targetSize - tmpTargetSize) * bidEntry.getPrice();
                    tmpTargetSize += (targetSize - tmpTargetSize);
                } else if ((targetSize - tmpTargetSize) == 0) {
                    break;
                }
            }
        }
        //print output
        /// create an object of DecimalFormat class
        DecimalFormat df_obj = new DecimalFormat("#.##");
        tmpCurrBuyExpenseTotal = Double.valueOf(df_obj.format(tmpCurrBuyExpenseTotal));
        if (bidInstrCount >= targetSize && prevBuyExpenseTotal != tmpCurrBuyExpenseTotal) {
            prevBuyExpenseTotal = tmpCurrBuyExpenseTotal;
            prevBuySizeTotal = targetSize;
            System.out.println(String.format("%d %s %.2f", currOrderEntry.getTimestamp(), "S", tmpCurrBuyExpenseTotal));
        } else if (bidInstrCount <= targetSize && prevBuyExpenseTotal != tmpCurrBuyExpenseTotal && prevBuySizeTotal > tmpTargetSize) {
            prevBuyExpenseTotal = Double.NaN;
            prevBuySizeTotal = -1;
            System.out.println(String.format("%d %s %s", currOrderEntry.getTimestamp(), "S", "NA"));
        }
        return tmpCurrBuyExpenseTotal;
    }

    private double calculateIncomeNew(LimitOrderEntry currOrderEntry, int targetSize) {

        int i;
        if (currOrderEntry.getTimestamp() == 28800538 || currOrderEntry.getTimestamp() ==28800744) //32913787 32913788
            i = 1;

        int askInstrCount = countInstr(asks);
        double tmpCurrSellIncomeTotal = 0.0;
        int tmpTargetSize = 0;
        if (askInstrCount >= targetSize) {
            for (LimitOrderEntry askEntry : asks) {
                if ((targetSize - tmpTargetSize) >= askEntry.getSize()) {
                    tmpCurrSellIncomeTotal += (askEntry.getSize() * askEntry.getPrice());
                    tmpTargetSize += askEntry.getSize();
                } else if ((targetSize - tmpTargetSize) < askEntry.getSize() && (targetSize - tmpTargetSize) > 0) {
                    tmpCurrSellIncomeTotal += (targetSize - tmpTargetSize) * askEntry.getPrice();
                    tmpTargetSize += (targetSize - tmpTargetSize);
                } else if ((targetSize - tmpTargetSize) == 0) {
                    break;
                }
            }
        }
        //print output
        /// create an object of DecimalFormat class
        DecimalFormat df_obj = new DecimalFormat("#.##");
        tmpCurrSellIncomeTotal = Double.valueOf(df_obj.format(tmpCurrSellIncomeTotal));
        if (askInstrCount >= targetSize && prevSellIncomeTotal != tmpCurrSellIncomeTotal) {
            prevSellIncomeTotal = tmpCurrSellIncomeTotal;
            prevSellSizeTotal = targetSize;
            System.out.println(String.format("%d %s %.2f", currOrderEntry.getTimestamp(), "B", tmpCurrSellIncomeTotal));
        } else if (askInstrCount <= targetSize && prevSellIncomeTotal != tmpCurrSellIncomeTotal && prevSellSizeTotal > tmpTargetSize) {
            prevSellIncomeTotal = Double.NaN;
            prevSellSizeTotal = -1;
            System.out.println(String.format("%d %s %s", currOrderEntry.getTimestamp(), "B", "NA"));
        }
        return tmpCurrSellIncomeTotal;
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

    public int getOrderBookMapSize() {
        return orderBookMap.size();
    }

    public int getTotalBidSize() {
        return bids.size();
    }

    public int getTotalAskSize() {
        return asks.size();
    }

}
