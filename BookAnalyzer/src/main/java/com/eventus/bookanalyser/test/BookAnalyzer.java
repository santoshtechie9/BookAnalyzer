package com.eventus.bookanalyser.test;

import com.eventus.bookanalyser.comparator.AskComparator;
import com.eventus.bookanalyser.comparator.BidComparator;
import com.eventus.bookanalyser.app.OrderBookOld;
import com.eventus.bookanalyser.model.LimitOrderEntry;

import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class BookAnalyzer {
    public static void main(String[] args) {

        System.out.println("Enter input:");
        // Using Scanner for Getting Input from User
        Scanner in = new Scanner(System.in);
/*
        String dataLog = in.nextLine();
        if (dataLog.isEmpty() || dataLog.isBlank()) {
            throw new IllegalArgumentException();
        }
*/

        LimitOrderEntry bo1 = new LimitOrderEntry(1632610775496L, "A", "ord-1", "B", 10, 9);
        LimitOrderEntry bo2 = new LimitOrderEntry(1632610775489L, "A", "ord-2", "B", 20, 19);
        LimitOrderEntry bo3 = new LimitOrderEntry(1632610775490L, "A", "ord-3", "B", 30, 29);
        LimitOrderEntry bo4 = new LimitOrderEntry(1632610775491L, "A", "ord-4", "B", 40, 39);
        LimitOrderEntry bo5 = new LimitOrderEntry(1632610775492L, "A", "ord-5", "B", 50, 49);
        LimitOrderEntry bo6 = new LimitOrderEntry(1632610775493L, "A", "ord-6", "B", 6, 59);

        Set<LimitOrderEntry> bids = new TreeSet<>(new BidComparator());
        bids.add(bo3);
        bids.add(bo4);
        bids.add(bo6);
        bids.add(bo5);
        bids.add(bo2);
        bids.add(bo1);

        bids.remove(bo1);
        for (LimitOrderEntry ord : bids) {
            System.out.println(ord.toString());
        }


        SortedSet<LimitOrderEntry> asks = new TreeSet<>(new AskComparator());
        LimitOrderEntry o1 = new LimitOrderEntry(1632610775496L, "A", "ord-1", "S", 10, 85.4);
        LimitOrderEntry o3 = new LimitOrderEntry(1632610775490L, "A", "ord-3", "S", 30, 8.6);
        LimitOrderEntry o4 = new LimitOrderEntry(1632610775489L, "A", "ord-4", "S", 10, 85.3);
        LimitOrderEntry o2 = new LimitOrderEntry(1632610775489L, "A", "ord-2", "S", 20, 85.4);
        LimitOrderEntry o5 = new LimitOrderEntry(1632610775489L, "A", "ord-5", "S", 5, 85.4);

        asks.add(o2);
        asks.add(o1);
        asks.add(o3);
        asks.add(o4);

        o2 = new LimitOrderEntry(1632610775489L, "A", "ord-2", "S", 2, 85.4);
        asks.remove(o2);
        //asks.add(o5);
        asks.add(o2);

        //o2 = new Order(1632610775489L, "A", "ord-2", "S", 20, 20);
        //asks.add(o2);
        //asks.

        //boolean isRemoved = asks.remove(o4);
        //System.out.println("remove {}" + isRemoved);

        System.out.println("**************************************");

        for (LimitOrderEntry ord : asks) {
            System.out.println(ord.toString());
        }

        OrderBookOld orderBook = new OrderBookOld();



/*
        while (!dataLog.equalsIgnoreCase("exit!")) {
            System.out.println("**********************************");
            System.out.println(dataLog);
            System.out.println("**********************************");
            System.out.println("Enter input:");
            dataLog = in.nextLine();
        }

    }
*/

    }
}
