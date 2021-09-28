package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.datastructure.LimitOrderBook;
import com.eventus.bookanalyser.datastructure.OrderTypes;
import com.eventus.bookanalyser.model.LimitOrderEntry;

import java.util.Arrays;
import java.util.List;

public class BookAnalyzer {

    private double currentIncome;
    private double currentExpense;
    private final LimitOrderBook orderBook = new LimitOrderBook("ZING");

    public void run(String dataLog) {
        LimitOrderEntry limitOrderEntry = null;
        List<String> dataLogArray = Arrays.asList(dataLog.split(" "));
        //fast fail checks
        hasValidNumberOfFields(dataLogArray);
        isValidField(dataLogArray);

        if (isAddOrder(dataLogArray))
            limitOrderEntry = creteAddOrderEntry(dataLogArray);
        else if (isRemoveOrder(dataLogArray))
            limitOrderEntry = creteRemoveOrderEntry(dataLogArray);

        //System.out.println("Valid data log");
        processOrder(limitOrderEntry);

    }

    private boolean isRemoveOrder(List<String> dataLogArray) {
        String orderType = dataLogArray.get(1);
        return orderType.equalsIgnoreCase(OrderTypes.R.name());
    }

    private boolean isRemoveOrder(LimitOrderEntry limitOrderEntry) {
        return limitOrderEntry.getOrderType().equalsIgnoreCase(OrderTypes.R.name());
    }


    private boolean isAddOrder(List<String> dataLogArray) {
        String orderType = dataLogArray.get(1);
        return orderType.equalsIgnoreCase(OrderTypes.A.name());
    }

    private boolean isAddOrder(LimitOrderEntry limitOrderEntry) {
        return limitOrderEntry.getOrderType().equalsIgnoreCase(OrderTypes.A.name());
    }

    private void isValidField(List<String> dataLogArray) {

        long timestamp = Long.valueOf(dataLogArray.get(0));
        String orderType = dataLogArray.get(1);
        String orderId = dataLogArray.get(2);


        if (!(orderType.equalsIgnoreCase("A") || orderType.equalsIgnoreCase("R")))
            throw new IllegalArgumentException(String.format("Invalid orderType: %s", orderType));

        if (orderType.equalsIgnoreCase("A")) {
            String side = dataLogArray.get(3);
            double price = Double.valueOf(dataLogArray.get(4));
            int size = Integer.valueOf(dataLogArray.get(5));
            if (!(timestamp >= 0))
                throw new IllegalArgumentException(String.format("Invalid timestamp: %d", timestamp));
            else if (orderId.isBlank() || orderId.isEmpty())
                throw new IllegalArgumentException(String.format("Invalid orderId: %s", orderId));
            else if (!(side.equalsIgnoreCase("B") || side.equalsIgnoreCase("S")))
                throw new IllegalArgumentException(String.format("Invalid side: %s", side));
            else if (price <= 0)
                throw new IllegalArgumentException(String.format("Invalid price: %f", price));
            else if (size <= 0)
                throw new IllegalArgumentException(String.format("Invalid size: %d", timestamp));
        } else {
            int size = Integer.valueOf(dataLogArray.get(3));
            if (!(timestamp >= 0))
                throw new IllegalArgumentException(String.format("Invalid timestamp: %d", timestamp));
            else if (orderId.isBlank() || orderId.isEmpty())
                throw new IllegalArgumentException(String.format("Invalid orderId: %s", orderId));
            else if (size <= 0)
                throw new IllegalArgumentException(String.format("Invalid size: %d", timestamp));
        }
    }

    public void hasValidNumberOfFields(List<String> dataLogArray) {
        int fieldCount = dataLogArray.size();
        String orderType = dataLogArray.get(1);
        if (orderType.equalsIgnoreCase(OrderTypes.A.name())) {
            if (fieldCount != 6)
                throw new IllegalArgumentException("Invalid argument; Add Order data log should contain 6 fields; space delimited!");
        } else if (orderType.equalsIgnoreCase(OrderTypes.R.name())) {
            if (fieldCount != 4)
                throw new IllegalArgumentException("Invalid row; Reduce Order data log should contain 4 fields; space delimited!");
        }
    }

    private LimitOrderEntry creteAddOrderEntry(List<String> dataLogArray) {
        long timestamp = Long.valueOf(dataLogArray.get(0));
        String orderType = dataLogArray.get(1);
        String orderId = dataLogArray.get(2);
        String side = dataLogArray.get(3);
        double price = Double.valueOf(dataLogArray.get(4));
        int size = Integer.valueOf(dataLogArray.get(5));
        LimitOrderEntry limitOrderEntry = new LimitOrderEntry(timestamp, orderType, orderId, side, price, size);
        return limitOrderEntry;
    }

    private LimitOrderEntry creteRemoveOrderEntry(List<String> dataLogArray) {
        long timestamp = Long.valueOf(dataLogArray.get(0));
        String orderType = dataLogArray.get(1);
        String orderId = dataLogArray.get(2);
        int size = Integer.valueOf(dataLogArray.get(3));
        LimitOrderEntry limitOrderEntry = new LimitOrderEntry(timestamp, orderType, orderId, null, null, size);
        return limitOrderEntry;
    }


    private void processOrder(LimitOrderEntry limitOrderEntry) {

        if (isAddOrder(limitOrderEntry)) {
            orderBook.addOrder(limitOrderEntry);
            //printExpense(limitOrderEntry);
        }

        if (isRemoveOrder(limitOrderEntry)) {
            orderBook.modifyOrder(limitOrderEntry);
            //printIncome(limitOrderEntry);
        }
    }

    public void printIncome(LimitOrderEntry orderEntry) {
        System.out.println(String.format("%l %s %d", orderEntry.getTimestamp(), orderEntry.getSide(), orderBook.calculateIncome(orderEntry, 100)));
    }

    void printExpense(LimitOrderEntry orderEntry) {
        System.out.println(String.format("%l %s %d", orderEntry.getTimestamp(), orderEntry.getSide(), orderBook.calculateExpense(orderEntry, 100)));
    }

}
