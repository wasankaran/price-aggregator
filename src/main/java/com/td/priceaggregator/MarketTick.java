package com.td.priceaggregator;

class MarketTick {
    private double bid;
    private double ask;

    double getAsk() {
        return ask;
    }

    void setAsk(double ask) {
        this.ask = ask;
    }

    double getBid() { return bid; }

    void setBid(double bid) {
        this.bid = bid;
    }

    MarketTick(double bid, double ask) {
        this.bid = bid;
        this.ask = ask;
    }
}
