package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.model.Order;

import java.util.Scanner;
import java.util.Set;
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

        Order o1 = new Order(1632610775496L, "A", "ord-1", "B", 10, 9);
        Order o2 = new Order(1632610775489L, "A", "ord-2", "B", 20, 19);
        Order o3 = new Order(1632610775490L, "A", "ord-3", "B", 30, 29);
        Order o4 = new Order(1632610775491L, "A", "ord-4", "B", 40, 39);
        Order o5 = new Order(1632610775492L, "A", "ord-5", "B", 50, 49);
        Order o6 = new Order(1632610775493L, "A", "ord-6", "B", 6, 59);

        Set<Order> bids = new TreeSet<>(new BidComparator());

        bids.add(o3);
        bids.add(o4);
        bids.add(o6);
        bids.add(o5);
        bids.add(o2);
        bids.add(o1);

        for (Order ord : bids) {
            System.out.println(ord.toString());
        }

        Set<Order> asks = new TreeSet<>(new AskComparator());
        o1 = new Order(1632610775496L, "A", "ord-1", "S", 10, 18.5);
        o2 = new Order(1632610775489L, "A", "ord-2", "S", 20, 85.4);
        o3 = new Order(1632610775490L, "A", "ord-3", "S", 30, 8.6);
        o4 = new Order(1632610775489L, "A", "ord-4", "S", 11, 85.4);

        asks.add(o1);
        asks.add(o2);
        asks.add(o3);
        asks.add(o4);

        asks.remove(o2);

        System.out.println("**************************************");

        for (Order ord : asks) {
            System.out.println(ord.toString());
        }

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
