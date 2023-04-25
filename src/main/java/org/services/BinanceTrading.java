package org.services;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerPrice;

import java.util.List;

public class BinanceTrading {
    private final String apiKey;
    private final String secretKey;
    private final BinanceApiRestClient client;

    public BinanceTrading(String apiKey, String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, secretKey);
        this.client = factory.newRestClient();
    }

    public double getPrice(String symbol) {
        TickerPrice tickerPrice = client.getPrice(symbol);
        return Double.parseDouble(tickerPrice.getPrice());
    }

    public NewOrderResponse createBuyOrder(String symbol, TimeInForce timeInForce, String quantity, String price) {
        NewOrder order = NewOrder.limitBuy(symbol, timeInForce, quantity, price);
        return client.newOrder(order);
    }

    public NewOrderResponse createSellOrder(String symbol, TimeInForce timeInForce, String quantity, String price) {
        NewOrder order = NewOrder.limitSell(symbol, timeInForce, quantity, price);
        return client.newOrder(order);
    }

    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval) {
        return client.getCandlestickBars(symbol, interval);
    }
}
