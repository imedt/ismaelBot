package org.entity;

import org.ta4j.core.AnalysisCriterion;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.Num;

public class TotalProfitCriterion implements AnalysisCriterion {

    private final Num initialCash;

    public TotalProfitCriterion(Num initialCash) {
        this.initialCash = initialCash;
    }

    @Override
    public Num calculate(TradingRecord tradingRecord) {
        Num totalProfit = tradingRecord.getTrades().stream()
                .map(trade -> calculateProfit(trade))
                .reduce(Num::plus)
                .orElse(null);

        if (totalProfit == null) {
            return null;
        }

        return totalProfit.dividedBy(initialCash);
    }

    private Num calculateProfit(Trade trade) {
        Num entryPrice = trade.getEntry().getPrice();
        Num exitPrice = trade.getExit().getPrice();
        Num entryAmount = trade.getEntry().getAmount();
        Num exitAmount = trade.getExit().getAmount();
        Num profit = exitPrice.multipliedBy(exitAmount).minus(entryPrice.multipliedBy(entryAmount));
        return profit.dividedBy(entryPrice.multipliedBy(entryAmount));
    }

    @Override
    public Num calculate(Trade trade) {
        Num entryPrice = trade.getEntry().getPrice();
        Num exitPrice = trade.getExit().getPrice();
        Num entryAmount = trade.getEntry().getAmount();
        Num exitAmount = trade.getExit().getAmount();
        Num profit = exitPrice.multipliedBy(exitAmount).minus(entryPrice.multipliedBy(entryAmount));
        return profit.dividedBy(entryPrice.multipliedBy(entryAmount));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
