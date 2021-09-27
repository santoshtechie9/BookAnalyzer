package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.model.LimitOrderEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LimitOrderBookTest {

    IOrderBook orderBook;
    LimitOrderEntry buy1;
    LimitOrderEntry buy2;
    LimitOrderEntry buy3;
    LimitOrderEntry sell1;
    LimitOrderEntry sell2;
    LimitOrderEntry sell3;

    @BeforeEach
    public void before() {
        orderBook = new LimitOrderBook("ZING");
        buy1 = new LimitOrderEntry(1632610775496L, "A", "buy1", "B", 50, 210);
        buy2 = new LimitOrderEntry(1632610775496L, "A", "buy2", "B", 100, 200);
        buy3 = new LimitOrderEntry(1632610775496L, "A", "buy3", "B", 150, 300);
        LimitOrderEntry buy4 = new LimitOrderEntry(1632610775496L, "A", "buy4", "B", 100, 400);
        sell1 = new LimitOrderEntry(1632610775496L, "A", "sell2", "S", 50, 1003);
        sell2 = new LimitOrderEntry(1632610775496L, "A", "sell2", "S", 100, 1004);
        sell3 = new LimitOrderEntry(1632610775496L, "A", "sell3", "S", 150, 1005);
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);
        orderBook.addOrder(buy3);
        orderBook.addOrder(buy4);
        orderBook.addOrder(sell2);
        orderBook.addOrder(sell2);
        orderBook.addOrder(sell3);
    }

    @Test
    void addOrder() {
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);
        orderBook.addOrder(buy3);
        orderBook = (LimitOrderBook) orderBook;
        //((LimitOrderBook) orderBook).getBidList().forEach(x -> System.out.println(x.toString()));
        Assertions.assertEquals(3, ((LimitOrderBook) orderBook).getBidList().size());
    }

    @Test
    void removeOrderBidList() {
        buy1 = new LimitOrderEntry(1632610775496L, "R", "buy1", "B", 0, 1004);
        orderBook.modifyOrder(buy1);
        //((LimitOrderBook) orderBook).getBidList().forEach(x -> System.out.println(x.toString()));
        Assertions.assertEquals(2, ((LimitOrderBook) orderBook).getBidList().size());
    }

    @Test
    void updateOrderBidList() {
        buy2 = new LimitOrderEntry(1632610775496L, "R", "buy2", "B", 140, 1110);
        orderBook.modifyOrder(buy2);
        //((LimitOrderBook) orderBook).getBidList().forEach(x -> System.out.println(x.toString()));
        Assertions.assertEquals(3, ((LimitOrderBook) orderBook).getBidList().size());
    }

    @Test
    void calculateExpenses(){
        double expense = ((LimitOrderBook)orderBook).getExpense(200);
        System.out.println("Final Expense : " + expense);
    }


/*
    @Test
    void modifyOrderBidList() {
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);
        orderBook.addOrder(buy3);
        buy2 = new LimitOrderEntry(1632610775496L, "R", "order2", "B", 10, 1004);
        orderBook.modifyOrder(buy2);
        ((LimitOrderBook) orderBook).getBidList().forEach(x -> System.out.println(x.toString()));
        //Assertions.assertEquals(3, ((LimitOrderBook) orderBook).getBidList().size());
    }
*/


}