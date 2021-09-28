package com.eventus.bookanalyser.model;

public class OutputEntry {

    private int bidInstrCount;
    private int askInstrCount;
    private Long timestamp;
    private String side;
    private String total;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getBidInstrCount() {
        return bidInstrCount;
    }

    public void setBidInstrCount(int bidInstrCount) {
        this.bidInstrCount = bidInstrCount;
    }

    public int getAskInstrCount() {
        return askInstrCount;
    }

    public void setAskInstrCount(int askInstrCount) {
        this.askInstrCount = askInstrCount;
    }
}
