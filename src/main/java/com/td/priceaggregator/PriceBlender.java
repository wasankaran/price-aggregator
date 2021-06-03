package com.td.priceaggregator;

public interface PriceBlender {
    /**
     * This method returns the best bid price
     * Returns a 0 if there are no price available or if the bid crosses the ask
     */
    double getBestBid();

    /**
     * This method returns the best ask price
     * Returns a 0 if there are no price available or if the bid crosses the ask
     */
    double getBestAsk();

    /**
     * This method returns the best mid price
     * Returns a 0 if there are no price available or if the bid crosses the ask
     */
    double getBestMid();

    /**
     * This method handles the tick data as received from different liquidity providers
     */
    void updatePrice(double bid, double ask, MarketSource source);
}
