package com.eventus.bookanalyser.datastructure;

import com.eventus.bookanalyser.model.LimitOrderEntry;

public interface IOrderBook {

    void addOrder(LimitOrderEntry limitOrderEntry);

    void modifyOrder(LimitOrderEntry limitOrderEntry);

}
