package com.eventus.bookanalyser.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LimitOrderEntryTest {
    private LimitOrderEntry ord1;
    private LimitOrderEntry ord2;

    @BeforeEach
    void setUp() {
        ord1 = new LimitOrderEntry(1632610775496L, "A", "ord1", "S", 18.5, 10);
        ord2 = new LimitOrderEntry(1632610775490L, "A", "ord2", "S", 8.6, 30);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testEquals() {
        ord2 = new LimitOrderEntry(1632610775490L, "A", "ord1", "S", 8.6, 30);
        Assertions.assertEquals(true, ord1.equals(ord2));
    }

    @Test
    void testHashCodeEquals() {
        ord2 = new LimitOrderEntry(1632610775490L, "A", "ord1", "S", 8.6, 30);
        System.out.println("ord1 : " + ord1.hashCode());
        System.out.println("ord2 : " + ord2.hashCode());
        Assertions.assertEquals(true, ord1.hashCode() == ord2.hashCode());
    }

    @Test
    void testHashCodeDifferent() {
        Assertions.assertEquals(false, ord1.hashCode() == ord2.hashCode());
    }

    @Test
    void getTimestamp() {
        Assertions.assertEquals(1632610775496L, ord1.getTimestamp());
    }

    @Test
    void getOrderType() {
        Assertions.assertEquals("A", ord1.getOrderType());
    }

    @Test
    void getOrderId() {
        Assertions.assertEquals(true, ord1.getOrderId().equalsIgnoreCase("ord1"));
    }

    @Test
    void getSide() {
        Assertions.assertEquals(true, ord1.getSide().equalsIgnoreCase("S"));
    }

    @Test
    void getPrice() {
        Assertions.assertEquals(18.5, ord1.getPrice());
    }

    @Test
    void getSize() {
        Assertions.assertEquals(30, ord2.getSize());

    }
}