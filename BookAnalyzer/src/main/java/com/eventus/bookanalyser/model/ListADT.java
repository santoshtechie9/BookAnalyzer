package com.eventus.bookanalyser.model;

import com.eventus.bookanalyser.app.Order;

import java.util.ArrayList;
import java.util.List;

public abstract class ListADT {

    private List<Order> list = new ArrayList<>();

    public abstract void insert(Order order);

    public abstract void delete(Order order);

    public abstract void modify(Order order);

    public abstract void sort();

    public List<Order> getList() {
        return this.list;
    }
}
