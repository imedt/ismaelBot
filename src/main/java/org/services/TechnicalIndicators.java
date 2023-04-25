package org.services;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.ParabolicSarIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class TechnicalIndicators {

    private final BarSeries barSeries;
    private ClosePriceIndicator closePriceIndicator;
    private HighPriceIndicator maxPriceIndicator;
    private LowPriceIndicator minPriceIndicator;
    private EMAIndicator emaIndicator;
    private BollingerBandsMiddleIndicator bbmIndicator;
    private BollingerBandsLowerIndicator bblIndicator;
    private BollingerBandsUpperIndicator bbuIndicator;

    public TechnicalIndicators(String symbol, CandlestickInterval interval, int emaTimeFrame, int bbTimeFrame, int bbNbDev) {
        // Récupérer les données historiques de la paire de trading
        BinanceTrading binanceTrading = new BinanceTrading("votreCléAPI", "votreSecretAPI");
        List<Candlestick> candlesticks = binanceTrading.getCandlestickBars(symbol, interval);

        this.barSeries = new BaseBarSeriesBuilder().withName(symbol).build();
        // Initialiser la série chronologique à partir des données historiques
        BaseBarSeriesBuilder builder = new BaseBarSeriesBuilder();
        for (Candlestick candlestick : candlesticks) {
            ZonedDateTime endTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(candlestick.getCloseTime()), ZoneId.systemDefault());
            Duration timePeriod = Duration.parse("PT" + interval.getIntervalId().toUpperCase());
            ZonedDateTime startTime = endTime.minus(timePeriod);
            BigDecimal openPrice = new BigDecimal(candlestick.getOpen());
            BigDecimal highPrice = new BigDecimal(candlestick.getHigh());
            BigDecimal lowPrice = new BigDecimal(candlestick.getLow());
            BigDecimal closePrice = new BigDecimal(candlestick.getClose());
            BigDecimal volume = new BigDecimal(candlestick.getVolume());
            Bar bar = new BaseBar(timePeriod, endTime, openPrice, highPrice, lowPrice, closePrice, volume);
            barSeries.addBar(bar);
        }

        this.closePriceIndicator = new ClosePriceIndicator(barSeries);
        this.maxPriceIndicator = new HighPriceIndicator(barSeries);
        this.minPriceIndicator = new LowPriceIndicator(barSeries);
        this.emaIndicator = new EMAIndicator(closePriceIndicator, emaTimeFrame);
        StandardDeviationIndicator standardDeviation = new StandardDeviationIndicator(closePriceIndicator, bbTimeFrame);
        this.bbmIndicator = new BollingerBandsMiddleIndicator(emaIndicator);
        this.bblIndicator = new BollingerBandsLowerIndicator(bbmIndicator, standardDeviation, barSeries.numOf(bbNbDev));
        this.bbuIndicator = new BollingerBandsUpperIndicator(bbmIndicator, standardDeviation, barSeries.numOf(bbNbDev));
    }

    public double getMaxPrice() {
        HighPriceIndicator maxPriceIndicator = new HighPriceIndicator(barSeries);
        return maxPriceIndicator.getValue(barSeries.getEndIndex()).doubleValue();
    }

    public double getMinPrice() {
        LowPriceIndicator minPriceIndicator = new LowPriceIndicator(barSeries);
        return minPriceIndicator.getValue(barSeries.getEndIndex()).doubleValue();
    }

    public double getClosePrice() {
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(barSeries);
        return closePriceIndicator.getValue(barSeries.getEndIndex()).doubleValue();
    }

    public double getOpenPrice() {
        OpenPriceIndicator openPriceIndicator = new OpenPriceIndicator(barSeries);
        return openPriceIndicator.getValue(barSeries.getEndIndex()).doubleValue();
    }

    public double getRSI(int timeFrame) {
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(barSeries);
        org.ta4j.core.indicators.RSIIndicator rsiIndicator = new RSIIndicator(closePriceIndicator, timeFrame);
        return rsiIndicator.getValue(barSeries.getEndIndex()).doubleValue();
    }

    public double getADX(int timeFrame) {
        ADXIndicator adxIndicator = new ADXIndicator(barSeries, timeFrame);
        return adxIndicator.getValue(barSeries.getEndIndex()).doubleValue();
    }

    public double getPSAR(double step, double maxStep) {
        Num af = barSeries.numOf(step);
        Num maxAf = barSeries.numOf(maxStep);
        ParabolicSarIndicator psarIndicator = new ParabolicSarIndicator(barSeries, af, maxAf);
        return psarIndicator.getValue(barSeries.getEndIndex()).doubleValue();
    }

    public double getEMA(int timeFrame) {
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(barSeries);
        EMAIndicator emaIndicator = new EMAIndicator(closePriceIndicator, timeFrame);
        return emaIndicator.getValue(barSeries.getEndIndex()).doubleValue();
    }

    public ClosePriceIndicator getClosePriceIndicator() {
        return closePriceIndicator;
    }

    public HighPriceIndicator getMaxPriceIndicator() {
        return maxPriceIndicator;
    }

    public LowPriceIndicator getMinPriceIndicator() {
        return minPriceIndicator;
    }

    public EMAIndicator getEmaIndicator() {
        return emaIndicator;
    }

    public BollingerBandsMiddleIndicator getBbmIndicator() {
        return bbmIndicator;
    }

    public BollingerBandsLowerIndicator getBblIndicator() {
        return bblIndicator;
    }

    public BollingerBandsUpperIndicator getBbuIndicator() {
        return bbuIndicator;
    }
    public BarSeries getBarSeries() {
        return this.barSeries;
    }

}
