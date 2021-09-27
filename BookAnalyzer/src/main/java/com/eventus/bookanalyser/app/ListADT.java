package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.model.LimitOrderEntry;

import java.util.ArrayList;
import java.util.List;

public abstract class ListADT {

    private List<LimitOrderEntry> list = new ArrayList<>();

    public abstract void insert(LimitOrderEntry limitOrderEntry);

    public abstract void delete(LimitOrderEntry limitOrderEntry);

    public abstract void modify(LimitOrderEntry limitOrderEntry);

    public abstract void sort();

    public List<LimitOrderEntry> getList() {
        return this.list;
    }
}
