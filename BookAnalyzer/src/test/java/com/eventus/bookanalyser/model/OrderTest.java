package com.eventus.bookanalyser.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OrderTest {
    private static LimitOrderEntry ord1;
    private static LimitOrderEntry ord2;

    @BeforeAll
    static void preTest() {
        ord1 = new LimitOrderEntry(1632610775496L, "A", "ord1", "S", 18.5, 10);
        ord2 = new LimitOrderEntry(1632610775490L, "A", "ord2", "S", 8.6, 30);
    }

    @Test
    void getOrderId() {
    }

    @Test
    void getPrice() {
    }

    @Test
    void testEquals() {
        System.out.println("equals Test: " + ord1.equals(ord2));
    }

    @Test
    void testHashCode() {
        System.out.println("ord1 : " + ord1.hashCode());
        System.out.println("ord2 : " + ord2.hashCode());
    }
}