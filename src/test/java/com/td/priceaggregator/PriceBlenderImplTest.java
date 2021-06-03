package com.td.priceaggregator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
    Thought of adding a few tests which confirm the concurrency but unsure what to assert on for get operations
 */
class PriceBlenderImplTest {

    @Test
    public void testGetBestBidAskMid_NoMarketData_ReturnsZeroPrice() {
        PriceBlender blender = new PriceBlenderImpl();
        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(0, bid);
        assertEquals(0, ask);
        assertEquals(0, mid);
    }


    @Test
    public void testGetBestBidAskMid_InvalidTick1_ReturnsZeroPrice() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(0, 23, MarketSource.SOURCE_B);
        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(0, bid);
        assertEquals(0, ask);
        assertEquals(0, mid);
    }

    @Test
    public void testGetBestBidAskMid_InvalidTick2_ReturnsZeroPrice() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(24, 23, MarketSource.SOURCE_B);
        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(0, bid);
        assertEquals(0, ask);
        assertEquals(0, mid);
    }

    @Test
    public void testGetBestBidAskMid_ValidTick_ReturnsNonZeroPrice() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(21, 23, MarketSource.SOURCE_B);
        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(21, bid);
        assertEquals(23, ask);
        assertEquals(22, mid);
    }

    @Test
    public void testGetBestBidAskMid_InvalidTick_ReturnsZeroPrice() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(0, 0, MarketSource.SOURCE_A);
        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(0, bid);
        assertEquals(0, ask);
        assertEquals(0, mid);
    }

    @Test
    public void testGetBestBidAskMid_ValidMarketData_ReturnsBestPrice() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(19, 21, MarketSource.SOURCE_A);
        blender.updatePrice(20, 23, MarketSource.SOURCE_B);
        blender.updatePrice(18, 22, MarketSource.SOURCE_C);
        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(20, bid);
        assertEquals(21, ask);
        assertEquals(20.5, mid);
    }

    @Test
    public void testGetBestBidAskMid_Crosses_ReturnsZeroPrice() {

        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(19, 22, MarketSource.SOURCE_A);
        blender.updatePrice(22, 23, MarketSource.SOURCE_B);
        blender.updatePrice(18, 21, MarketSource.SOURCE_C);
        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(0, bid);
        assertEquals(0, ask);
        assertEquals(0, mid);
    }

    @Test
    public void testGetBestBidAskMid_ZeroBidPriceUpdate_ExcludesInvalidTick() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(19, 21, MarketSource.SOURCE_A);
        blender.updatePrice(20, 23, MarketSource.SOURCE_B);
        blender.updatePrice(18, 22, MarketSource.SOURCE_C);
        blender.updatePrice(0, 23, MarketSource.SOURCE_B);

        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(19, bid);
        assertEquals(21, ask);
        assertEquals(20, mid);
    }

    @Test
    public void testGetBestBidAskMid_HandlesMultipleTicks_ReturnsBestPrice() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(19, 21, MarketSource.SOURCE_A);
        blender.updatePrice(20, 23, MarketSource.SOURCE_B);
        blender.updatePrice(18, 22, MarketSource.SOURCE_C);
        blender.updatePrice(20.5, 23.5, MarketSource.SOURCE_B);

        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(20.5, bid);
        assertEquals(21, ask);
        assertEquals(20.75, mid);
    }

    @Test
    public void testGetBestBidAskMid_CrossTick_ReturnsZeroPrice() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(19, 21, MarketSource.SOURCE_A);
        blender.updatePrice(20, 23, MarketSource.SOURCE_B);
        blender.updatePrice(18, 22, MarketSource.SOURCE_C);
        blender.updatePrice(22, 23.5, MarketSource.SOURCE_B);

        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(0, bid);
        assertEquals(0, ask);
        assertEquals(0, mid);
    }

    @Test
    public void testGetBestBidAskMid2_MultipleTicks_ReturnsBestPrice() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(19, 21, MarketSource.SOURCE_A);
        blender.updatePrice(20, 23, MarketSource.SOURCE_B);
        blender.updatePrice(18, 22, MarketSource.SOURCE_C);
        blender.updatePrice(20.5, 23.5, MarketSource.SOURCE_B);
        blender.updatePrice(20.5, 20.5, MarketSource.SOURCE_A);
        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(20.5, bid);
        assertEquals(20.5, ask);
        assertEquals(20.5, mid);
    }

    @Test
    public void testGetBestBidAskMid3_MultipleTicks_ReturnsBestPrice() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(19, 21, MarketSource.SOURCE_A);
        blender.updatePrice(20, 23, MarketSource.SOURCE_B);
        blender.updatePrice(18, 22, MarketSource.SOURCE_C);
        blender.updatePrice(20.5, 23.5, MarketSource.SOURCE_B);
        blender.updatePrice(20.5, 20.5, MarketSource.SOURCE_A);
        blender.updatePrice(21, 21.5, MarketSource.SOURCE_A);

        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(21, bid);
        assertEquals(21.5, ask);
        assertEquals(21.25, mid);
    }

    @Test
    public void testGetBestBidAskMid4_MultipleTicks_ReturnsBestPrice() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(19, 21, MarketSource.SOURCE_A);
        blender.updatePrice(20, 23, MarketSource.SOURCE_B);
        blender.updatePrice(18, 22, MarketSource.SOURCE_C);
        blender.updatePrice(20.5, 23.5, MarketSource.SOURCE_B);
        blender.updatePrice(20.5, 20.5, MarketSource.SOURCE_A);
        blender.updatePrice(21, 21.5, MarketSource.SOURCE_A);
        blender.updatePrice(21, 21.5, MarketSource.SOURCE_A);
        blender.updatePrice(21, 21.5, MarketSource.SOURCE_C);
        blender.updatePrice(21, 21.5, MarketSource.SOURCE_B);

        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(21, bid);
        assertEquals(21.5, ask);
        assertEquals(21.25, mid);
    }

    @Test
    public void testGetBestBidAskMid_MultipleTicks_ReturnsZeroSpread() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(19, 21, MarketSource.SOURCE_A);
        blender.updatePrice(20, 23, MarketSource.SOURCE_B);
        blender.updatePrice(18, 22, MarketSource.SOURCE_C);
        blender.updatePrice(20.5, 23.5, MarketSource.SOURCE_B);
        blender.updatePrice(20.5, 20.5, MarketSource.SOURCE_A);
        blender.updatePrice(21, 21.5, MarketSource.SOURCE_A);
        blender.updatePrice(22, 22.5, MarketSource.SOURCE_A);
        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(22, bid);
        assertEquals(22, ask);
        assertEquals(22, mid);
    }

    @Test
    public void testGetBestBidAskMid_MarketMove_ReturnsShiftedPrice() {
        PriceBlender blender = new PriceBlenderImpl();
        blender.updatePrice(19, 21, MarketSource.SOURCE_A);
        blender.updatePrice(20, 23, MarketSource.SOURCE_B);
        blender.updatePrice(18, 22, MarketSource.SOURCE_C);
        blender.updatePrice(20.5, 23.5, MarketSource.SOURCE_B);
        blender.updatePrice(20.5, 20.5, MarketSource.SOURCE_A);
        blender.updatePrice(21, 21.5, MarketSource.SOURCE_A);
        blender.updatePrice(22.5, 23.5, MarketSource.SOURCE_A);

        double bid = blender.getBestBid();
        double ask = blender.getBestAsk();
        double mid = blender.getBestMid();

        assertEquals(0, bid);
        assertEquals(0, ask);
        assertEquals(0, mid);

        blender.updatePrice(22.5, 23.5, MarketSource.SOURCE_C);
        blender.updatePrice(23, 24, MarketSource.SOURCE_B);
        bid = blender.getBestBid();
        ask = blender.getBestAsk();
        mid = blender.getBestMid();

        assertEquals(23, bid);
        assertEquals(23.5, ask);
        assertEquals(23.25, mid);
    }
}