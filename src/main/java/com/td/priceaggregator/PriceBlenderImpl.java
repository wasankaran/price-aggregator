package com.td.priceaggregator;

import java.util.*;
import java.util.concurrent.*;


public class PriceBlenderImpl implements PriceBlender {

    private final Map<MarketSource, MarketTick> priceMap = new ConcurrentHashMap<>();
    private static final MarketTick EMPTY_PRICE = new MarketTick(0, 0);

    /* As ConcurrentHashMap views are weakly-consistent, there is a possibility that calls inside getBestPrice to
       calculate maxBid & minAsk may pick different tick data. Assuming that is acceptable. Else would need to create
       a disconnected copy. copyOf is a factory method and may create new instances or reuse existing ones.
     */
    @Override
    public double getBestBid() {
        return PriceCalculator.getBestPrice(priceMap.values(), PriceSide.BID);
    }

    /* Assumption is there will be frequent market ticks, and more price updates compared to the get calls
       If not, will need to add a caching optimization such that last get request is cached and return
       straight away if the price has not ticked. That will be tricky and likely result in more blocking calls.
    */
    @Override
    public double getBestAsk() {
        return PriceCalculator.getBestPrice(priceMap.values(), PriceSide.ASK);
    }

    @Override
    public double getBestMid() {
        return PriceCalculator.getBestPrice(priceMap.values(), PriceSide.MID);
    }

    /** The implementation is optimised for faster processing of market data as compared to retrievals **/
    @Override
    public void updatePrice(double bid, double ask, MarketSource source) {
        priceMap.compute(source, (k, v) -> {
            if (PriceCalculator.isInvalidPrice(bid, ask) || PriceCalculator.isCross(bid, ask)) {
                return EMPTY_PRICE;
            } else {
                if (v == null) {
                    return new MarketTick(bid, ask);
                }
                v.setBid(bid); // re-use the MarketTick mutable
                v.setAsk(ask);
                return v;
            }
        });
    }

    private static class PriceCalculator {
        private static final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        private static double getBestPrice(Collection<MarketTick> prices, PriceSide side) {
            if (isPriceUnavailable(prices)) return 0;

            Future<Double> maxBidTask = submitMaxBidTask(prices);
            Future<Double> minAskTask = submitMinAskTask(prices);

            /* gets are somewhat compute-bound but that is a trade-off with less contention
               and keeping it simple
             */
            double bestBid, bestAsk;
            try {
                bestBid = maxBidTask.get();
                bestAsk = minAskTask.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return 0;
            }

            /* Assuming that in case bid/ask crosses we cannot form a price; we return 0
               An alternate could be that we check on each market tick that if a specific tick results in a cross
               and ignore the source price and give a price based on other source(s) till we receive new tick data.
            */
            if (isCross(bestBid, bestAsk)) return 0;

            /* Using a switch to avoid newing up a MarketTick object and return that (each time)
               and instead work with primitive double
            */
            return switch (side) {
                case BID -> bestBid;
                case ASK -> bestAsk;
                case MID -> (bestBid + bestAsk) / 2;
            };
        }

        private static Future<Double> submitMaxBidTask(Collection<MarketTick> prices) {
            return service.submit(() -> prices.stream()
                    .filter(p -> p.getBid() != 0)
                    .map(MarketTick::getBid)
                    .max(Comparator.naturalOrder())
                    .orElse(0.0));
        }

        private static Future<Double> submitMinAskTask(Collection<MarketTick> prices) {
            return service.submit(() -> prices.stream()
                    .filter(p -> p.getAsk() != 0)
                    .map(MarketTick::getAsk)
                    .min(Comparator.naturalOrder())
                    .orElse(0.0));
        }

        private static boolean isPriceUnavailable(Collection<MarketTick> prices){
            return prices.size() == 0;
        }

        private static boolean isInvalidPrice(double bid, double ask) {
            return (bid == 0 || ask == 0);
        }

        private static boolean isCross(double bid, double ask) {
            return (bid > ask);
        }

        private static void shutdown() {
            service.shutdown();
        }
    }
}
