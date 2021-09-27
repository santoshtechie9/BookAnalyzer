package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.model.LimitOrderEntry;

import java.util.Arrays;
import java.util.List;

public class BookAnalyzer {

    private double currentIncome;
    private double currentExpense;

    public void run(String dataLog) {
        List<String> dataLogArray = Arrays.asList(dataLog.split(" "));
        isValidNumberOfFields(dataLogArray);
        isValidField(dataLogArray);
        LimitOrderEntry limitOrderEntry = creteOrderEntry(dataLogArray);
    }

    private void isValidField(List<String> dataLogArray) {

        long timestamp = Long.valueOf(dataLogArray.get(0));
        String orderType= dataLogArray.get(1);
        String orderId = dataLogArray.get(2);
        String side = dataLogArray.get(3);
        double price = Double.valueOf(dataLogArray.get(4));
        int size = Integer.valueOf(dataLogArray.get(5));

        if (!(orderType.equalsIgnoreCase("A") || orderType.equalsIgnoreCase("R")))
            throw new IllegalArgumentException(String.format("Invalid orderType: %s", orderType));

        if(orderType.equalsIgnoreCase("A")) {
            if (!(timestamp >= 0))
                throw new IllegalArgumentException(String.format("Invalid timestamp: %d", timestamp));
            else if (orderId.isBlank() || orderId.isEmpty())
                throw new IllegalArgumentException(String.format("Invalid orderId: %s", orderId));
            else if (!(side.equalsIgnoreCase("B") || side.equalsIgnoreCase("S")))
                throw new IllegalArgumentException(String.format("Invalid side: %s", side));
            else if (price <= 0)
                throw new IllegalArgumentException(String.format("Invalid price: %f", price));
            else if (size <=0)
                throw new IllegalArgumentException(String.format("Invalid size: %d", timestamp));
        } else{
            if (!(timestamp >= 0))
                throw new IllegalArgumentException(String.format("Invalid timestamp: %d", timestamp));
            else if (orderId.isBlank() || orderId.isEmpty())
                throw new IllegalArgumentException(String.format("Invalid orderId: %s", orderId));
            else if (size <= 0)
                throw new IllegalArgumentException(String.format("Invalid size: %d", timestamp));
        }

    }

    public void isValidNumberOfFields(List<String> dataLogArray) {
        int fieldCount = dataLogArray.size();
        String orderType = dataLogArray.get(1);
        if (orderType == "A") {
            if (fieldCount != 6)
                throw new IllegalArgumentException("Invalid argument; Add Order data log should contain 6 fields; space delimited!");
        } else if (orderType == "R") {
            if (fieldCount != 4)
                throw new IllegalArgumentException("Invalid row; Reduce Order data log should contain 4 fields; space delimited!");
        }
    }

    public LimitOrderEntry creteOrderEntry(List<String> dataLogArray) {
        long timestamp = Long.valueOf(dataLogArray.get(0));
        String orderType= dataLogArray.get(1);
        String orderId = dataLogArray.get(2);
        String side = dataLogArray.get(3);
        double price = Double.valueOf(dataLogArray.get(4));
        int size = Integer.valueOf(dataLogArray.get(5));
        LimitOrderEntry limitOrderEntry = new LimitOrderEntry(timestamp,orderType,orderId,side,price,size);

        return null;
    }

    public void manageOrderEntries() {
    }

    public void printIncome(double income) {
    }

    void printExpense(double expense) {
    }

}
