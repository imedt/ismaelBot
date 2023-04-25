package org.example;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import org.entity.Result;
import org.entity.StrategyConfig;
import org.services.TechnicalIndicators;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException {
        // Récupération des données du BTC
        BinanceApiClient client = new BinanceApiClient();
        List<Candlestick> candlesticks = client.getCandlestickBars("BTCUSDT", CandlestickInterval.HOURLY, 1000);
        BarSeries barSeries = new BaseBarSeries();
        for (Candlestick candlestick : candlesticks) {
            barSeries.addBar(
                    ZonedDateTime.parse(candlestick.getCloseTime()),
                    Double.parseDouble(candlestick.getOpen()),
                    Double.parseDouble(candlestick.getHigh()),
                    Double.parseDouble(candlestick.getLow()),
                    Double.parseDouble(candlestick.getClose()),
                    Double.parseDouble(candlestick.getVolume()),
                    Double.parseDouble(candlestick.getQuoteAssetVolume())
            );
        }

        // Génération des configurations
        List<StrategyConfig> configs = generateConfigs();

        // Backtest des configurations
        List<Result> results = new ArrayList<>();
        for (StrategyConfig config : configs) {
            TechnicalIndicators indicators = new TechnicalIndicators(
                    barSeries,
                    config.getEmaTimeFrame(),
                    config.getBbTimeFrame(),
                    config.getBbNbDev()
            );
            BollingerBandsMiddleIndicator bbmIndicator = indicators.getBbmIndicator();
            BollingerBandsLowerIndicator bblIndicator = indicators.getBblIndicator();
            BollingerBandsUpperIndicator bbuIndicator = indicators.getBbuIndicator();
            BaseStrategy strategy = new BaseStrategy(
                    new CrossedDownIndicatorRule(bbmIndicator, bblIndicator),
                    new CrossedUpIndicatorRule(bbmIndicator, bbuIndicator)
            );
            TradingRecord tradingRecord = barSeries.run(strategy);
            TotalProfitCriterion profitCriterion = new TotalProfitCriterion();
            double profit = profitCriterion.calculate(barSeries, tradingRecord);
            results.add(new Result(config, profit));
        }

        // Affichage des résultats
        Path file = Paths.get("results.txt");
        StringBuilder sb = new StringBuilder();
        for (Result result : results) {
            sb.append(result.getConfig().toString())
                    .append(" - Profit: ")
                    .append(result.getProfit())
                    .append("\n");
        }
        Files.writeString(file, sb.toString());
    }

    private static List<StrategyConfig> generateConfigs() {
        int[] emaTimeFrames = {10, 15, 20, 25, 30, 35, 40, 45, 50};
        int[] bbTimeFrames = {10, 15, 20, 25, 30, 35, 40, 45, 50};
        int[] bbNbDevs = {1, 2, 3};

        List<StrategyConfig> configs = IntStream.of(emaTimeFrames)
                .boxed()
                .flatMap(emaTimeFrame -> IntStream.of(bbTimeFrames)
                        .boxed()
                        .flatMap(bbTimeFrame -> IntStream.of(bbNbDevs)
                                .boxed()
                                .map(bbNbDev -> new StrategyConfig(emaTimeFrame, bbTimeFrame, bbNbDev))))
                .collect(Collectors.toList());

        return configs;
    }
}
