/**
 * BBD Service Inc
 * All Rights Reserved @2018
 */
package com.jinghua.trade;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.market.OrderBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author jinghua
 * @version $Id: BinanceTrader.java, v0.1 2018/11/13 17:04 jinghua Exp $$
 */
public class BinanceTrader {

    private static Logger LOGGER = LoggerFactory.getLogger(BinanceTrader.class);

    private final double tradeDifference;
    private final double tradeProfit;
    private final int tradeAmount;
    private TradingClient client;
    private Double currentlyBoughtPrice;
    private Long orderId;
    private int panicBuyCounter;
    private int panicSellCounter;
    private double trackingLastPrice;

    BinanceTrader(double tradeDifference, double tradeProfit, int tradeAmount, String baseCurrency, String tradeCurrency, String key, String secret) {
        client = new TradingClient(baseCurrency, tradeCurrency, key, secret);
        trackingLastPrice = client.lastPrice();
        this.tradeAmount = tradeAmount;
        this.tradeProfit = tradeProfit;
        this.tradeDifference = tradeDifference;
        clear();
    }

    void tick() {
        double lastPrice = 0;
        try {
            OrderBook orderBook = client.getOrderBook();
            lastPrice = client.lastPrice();
            AssetBalance tradingBalance = client.getTradingBalance();
            double lastKnownTradingBalance = client.getAllTradingBalance();
            double lastBid = Double.valueOf(orderBook.getBids().get(0).getPrice());
            double lastAsk = Double.valueOf(orderBook.getAsks().get(0).getPrice());
            double buyPrice = lastBid + tradeDifference;
            double sellPrice = lastAsk - tradeDifference;
            double profitablePrice = buyPrice + (buyPrice * tradeProfit / 100);


            LOGGER.info(String.format("buyPrice:%.8f sellPrice:%.8f bid:%.8f ask:%.8f price:%.8f profit:%.8f diff:%.8f\n", buyPrice, sellPrice, lastAsk, lastAsk, lastPrice, profitablePrice, (lastAsk - profitablePrice)));

            if (orderId == null) {
                LOGGER.info("nothing bought, let`s check");
                // find a burst to buy
                // but make sure price is ascending!
                if (lastAsk >= profitablePrice) {
                    if (lastPrice > trackingLastPrice) {
                        LOGGER.info("Buy burst detected");
                        currentlyBoughtPrice = profitablePrice;
                        orderId = client.buy(tradeAmount, buyPrice).getOrderId();
                        panicBuyCounter = 0;
                        panicSellCounter = 0;
                    } else {
                        LOGGER.warn("woooops, price is falling?!? don`t do something!");
                        panicSellForCondition(lastPrice, lastKnownTradingBalance, client.tradingBalanceAvailable(tradingBalance));
                    }
                } else {
                    LOGGER.info(String.format("No profit detected, difference %.8f\n", lastAsk - profitablePrice));
                    currentlyBoughtPrice = null;
                    panicSellForCondition(lastPrice, lastKnownTradingBalance, client.tradingBalanceAvailable(tradingBalance));
                }
            } else {
                Order order = client.getOrder(orderId);
                OrderStatus status = order.getStatus();
                if (status != OrderStatus.CANCELED) {
                    // not new and not canceled, check for profit
                    LOGGER.info("Tradingbalance: " + tradingBalance);
                    if ("0".equals("" + tradingBalance.getLocked().charAt(0)) &&
                            lastAsk >= currentlyBoughtPrice) {
                        if (status == OrderStatus.NEW) {
                            // nothing happened here, maybe cancel as well?
                            panicBuyCounter++;
                            LOGGER.info(String.format("order still new, time %d\n", panicBuyCounter));
                            if (panicBuyCounter > 4) {
                                client.cancelOrder(orderId);
                                clear();
                            }
                        } else {
                            if ("0".equals("" + tradingBalance.getFree().charAt(0))) {
                                LOGGER.warn("no balance in trading money, clearing out");
                                clear();
                            } else if (status == OrderStatus.PARTIALLY_FILLED || status == OrderStatus.FILLED) {
                                LOGGER.info("Order filled with status " + status);
                                if (lastAsk >= profitablePrice) {
                                    LOGGER.info("still gaining profitable profits HODL!!");
                                } else {
                                    LOGGER.info("Not gaining enough profit anymore, let`s sell");
                                    LOGGER.info(String.format("Bought %d for %.8f and sell it for %.8f, this is %.8f coins profit", tradeAmount, currentlyBoughtPrice, sellPrice, (1.0 * currentlyBoughtPrice - sellPrice) * tradeAmount));
                                    client.sell(tradeAmount, sellPrice);
                                }
                            } else {
                                // WTF?!
                                LOGGER.error("DETECTED WTF!!!!!");
                                LOGGER.error("Order: " + order + " , Order-Status: " + status);
                                client.panicSell(lastKnownTradingBalance, lastPrice);
                                clear();
                            }
                        }
                    } else {
                        panicSellCounter++;
                        LOGGER.info(String.format("sell request not successful, increasing time %d\n", panicSellCounter));
                        panicSellForCondition(lastPrice, lastKnownTradingBalance, panicSellCounter > 3);
                    }
                } else {
                    LOGGER.warn("Order was canceled, cleaning up.");
                    clear(); // Order was canceled, so clear and go on
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to perform ticker", e);
        }
        trackingLastPrice = lastPrice;
    }

    private void panicSellForCondition(double lastPrice, double lastKnownTradingBalance, boolean condition) {
        if (condition) {
            LOGGER.info("panicSellForCondition");
            client.panicSell(lastKnownTradingBalance, lastPrice);
            clear();
        }
    }

    private void clear() {
        panicBuyCounter = 0;
        panicSellCounter = 0;
        orderId = null;
        currentlyBoughtPrice = null;
    }

    List<AssetBalance> getBalances() {
        return client.getBalances();
    }
}
