package org.entity;

public class StrategyConfig {
    private int emaTimeFrame;
    private int bbTimeFrame;
    private int bbNbDev;

    public StrategyConfig(int emaTimeFrame, int bbTimeFrame, int bbNbDev) {
        this.emaTimeFrame = emaTimeFrame;
        this.bbTimeFrame = bbTimeFrame;
        this.bbNbDev = bbNbDev;
    }

    public int getEmaTimeFrame() {
        return emaTimeFrame;
    }

    public int getBbTimeFrame() {
        return bbTimeFrame;
    }

    public int getBbNbDev() {
        return bbNbDev;
    }

    @Override
    public String toString() {
        return "EMA Time Frame : " + emaTimeFrame + " | BB Time Frame : " + bbTimeFrame + " | BB Nb Dev : " + bbNbDev;
    }
}
