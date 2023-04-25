package org.entity;

import org.services.TechnicalIndicators;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.ParabolicSarIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

public class Condition {
    private TechnicalIndicators technicalIndicators;

    public Condition(TechnicalIndicators technicalIndicators) {
        this.technicalIndicators = technicalIndicators;
    }

    public double getRSI(int timeFrame) {
        RSIIndicator rsiIndicator = new RSIIndicator(new ClosePriceIndicator(technicalIndicators.getBarSeries()), timeFrame);
        return rsiIndicator.getValue(technicalIndicators.getBarSeries().getEndIndex()).doubleValue();
    }

    public double getADX(int timeFrame) {
        ADXIndicator adxIndicator = new ADXIndicator(technicalIndicators.getBarSeries(), timeFrame);
        return adxIndicator.getValue(technicalIndicators.getBarSeries().getEndIndex()).doubleValue();
    }

    public double getPSAR(double step, double max) {
        BarSeries barSeries = technicalIndicators.getBarSeries();
        Num af = barSeries.numOf(step);
        Num maxAf = barSeries.numOf(max);
        ParabolicSarIndicator psarIndicator = new ParabolicSarIndicator(barSeries, af, maxAf);
        return psarIndicator.getValue(barSeries.getEndIndex()).doubleValue();
    }

    public double getEMA(int timeFrame) {
        EMAIndicator emaIndicator = new EMAIndicator(new ClosePriceIndicator(technicalIndicators.getBarSeries()), timeFrame);
        return emaIndicator.getValue(technicalIndicators.getBarSeries().getEndIndex()).doubleValue();
    }
}
