package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.datastructure.LimitOrderBook;
import com.eventus.bookanalyser.model.LimitOrderEntry;
import com.sun.jdi.request.DuplicateRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LimitOrderBookTest {

    LimitOrderBook orderBook;
    LimitOrderEntry buy1;
    LimitOrderEntry buy2;
    LimitOrderEntry buy3;
    LimitOrderEntry buy4;
    LimitOrderEntry sell1;
    LimitOrderEntry sell2;
    LimitOrderEntry sell3;
    LimitOrderEntry sell4;

    @BeforeEach
    public void before() {
        orderBook = new LimitOrderBook("ZING");
        buy1 = new LimitOrderEntry(1632610775496L, "A", "buy1", "B", 50.0, 100);
        buy2 = new LimitOrderEntry(1632610775496L, "A", "buy2", "B", 100.0, 200);
        buy3 = new LimitOrderEntry(1632610775496L, "A", "buy3", "B", 150.0, 30);
        buy4 = new LimitOrderEntry(1632610775496L, "A", "buy4", "B", 100.0, 10);

        sell1 = new LimitOrderEntry(1632610775496L, "A", "sell1", "S", 50.0, 100);
        sell2 = new LimitOrderEntry(1632610775496L, "A", "sell2", "S", 100.0, 200);
        sell3 = new LimitOrderEntry(1632610775496L, "A", "sell3", "S", 150.0, 300);
        sell4 = new LimitOrderEntry(1632610775496L, "A", "sell4", "S", 200.0, 400);

    }

    @Test
    void addOrderToBids() {
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);
        orderBook.addOrder(buy3);
        orderBook.addOrder(buy4);
        orderBook.printBidList();
        Assertions.assertEquals(4, orderBook.getTotalBidSize());
    }

    @Test
    void modifyOrderInBids() {
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);
        orderBook.addOrder(buy3);
        orderBook.addOrder(buy4);
        buy1 = new LimitOrderEntry(1632610775496L, "R", "buy1", 50);
        orderBook.modifyOrder(buy1);
        orderBook.printBidList();
        Assertions.assertEquals(4, orderBook.getTotalBidSize());
    }

    @Test
    void removeOrderFromBids() {
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);
        orderBook.addOrder(buy3);
        orderBook.addOrder(buy4);
        buy2 = new LimitOrderEntry(1632610775496L, "R", "buy2", 300);
        orderBook.modifyOrder(buy2);
        orderBook.printBidList();
        orderBook.printAskList();
        Assertions.assertEquals(3, orderBook.getTotalBidSize());
    }

    @Test
    void calculateExpenses() {
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);
        orderBook.addOrder(buy3);
        double expense = orderBook.calculateExpense(250);
        System.out.println("Final Expense : " + expense);
        Assertions.assertEquals(25500.0, expense);

    }

    @Test
    void addOrderToAsks() {
        orderBook.addOrder(sell1);
        orderBook.addOrder(sell2);
        orderBook.addOrder(sell3);
        orderBook.addOrder(sell4);
        orderBook.printAskList();
        Assertions.assertEquals(4, orderBook.getTotalAskSize());
    }

    @Test
    void modifyOrderInAsks() {
        orderBook.addOrder(sell1);
        orderBook.addOrder(sell2);
        orderBook.addOrder(sell3);
        orderBook.addOrder(sell4);
        sell2 = new LimitOrderEntry(1632610775496L, "R", "sell1", 40);
        orderBook.modifyOrder(sell2);
        orderBook.printAskList();
        Assertions.assertEquals(4, orderBook.getTotalAskSize());
    }

    @Test
    void removeOrderFromAsks() {
        orderBook.addOrder(sell1);
        orderBook.addOrder(sell2);
        orderBook.addOrder(sell3);
        orderBook.addOrder(sell4);
        sell2 = new LimitOrderEntry(1632610775496L, "R", "sell2", 300);
        orderBook.modifyOrder(sell2);
        orderBook.printBidList();
        orderBook.printAskList();
        Assertions.assertEquals(3, orderBook.getTotalAskSize());
    }

    @Test
    void calculateIncome() {
        orderBook.addOrder(sell1);
        orderBook.addOrder(sell3);
        orderBook.addOrder(sell4);
        orderBook.addOrder(sell2);
        orderBook.printAskList();
        double income = orderBook.calculateIncome(200);
        System.out.println("Final income : " + income);
        Assertions.assertEquals(40000.0, income);

    }

    @Test
    public void duplicateBidTest(){
        orderBook.addOrder(buy1);
        orderBook.addOrder(buy2);
        orderBook.addOrder(buy3);
        orderBook.addOrder(buy4);
        buy4 = new LimitOrderEntry(1632610775496L, "A", "buy4", 300);
        Assertions.assertThrows(DuplicateRequestException.class, () -> {
            orderBook.addOrder(buy4);
        });
    }

    @Test
    public void duplicateAskTest(){
        orderBook.addOrder(sell1);
        orderBook.addOrder(sell2);
        orderBook.addOrder(sell3);
        orderBook.addOrder(sell4);
        sell2 = new LimitOrderEntry(1632610775496L, "A", "sell2", 300);
        Assertions.assertThrows(DuplicateRequestException.class, () -> {
            orderBook.addOrder(sell2);
        });

    }



}