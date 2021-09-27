package com.eventus.bookanalyser.app;

import com.eventus.bookanalyser.model.LimitOrderEntry;

public interface IOrderBook {

    public void addOrder(LimitOrderEntry limitOrderEntry);

    public void modifyOrder(LimitOrderEntry limitOrderEntry);

}
