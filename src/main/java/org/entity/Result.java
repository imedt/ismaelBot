package org.entity;

public class Result {
    private StrategyConfig config;
    private double profit;

    public Result(StrategyConfig config, double profit) {
        this.config = config;
        this.profit = profit;
    }

    public StrategyConfig getConfig() {
        return config;
    }

    public double getProfit() {
        return profit;
    }

    @Override
    public String toString() {
        return "Configuration : " + config.toString() + "\nProfit : " + profit;
    }
}
