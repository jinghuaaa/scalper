/**
 * BBD Service Inc
 * All Rights Reserved @2018
 */
package com.jinghua.utils;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.TimeInForce;
import com.jinghua.constants.StorageConstant;
import com.jinghua.enums.TradeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.binance.api.client.domain.account.NewOrder.limitBuy;
import static com.binance.api.client.domain.account.NewOrder.limitSell;

/**
 * @author jinghua
 * @version $Id: MonitorUtil.java, v0.1 2018/11/13 11:30 jinghua Exp $$
 */
public class MonitorUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorUtil.class);

    static Boolean isTrading = false;

    static BigDecimal x1 = new BigDecimal("0.01");
    static BigDecimal xx1 = new BigDecimal("0.001");

    static BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("apikey", "secret");
    static BinanceApiAsyncRestClient client = factory.newAsyncRestClient();
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    //实际成交额不得低于预期的99.8%
    static BigDecimal amoutRate = new BigDecimal("0.997");


    public static void doRoute(String tokenName,
                               BigDecimal TOKENBTCbidLocal, BigDecimal TOKENBTCbidVolLocal, BigDecimal TOKENBTCaskLocal, BigDecimal TOKENBTCaskVolLocal,
                               BigDecimal TOKENETHbidLocal, BigDecimal TOKENETHbidVolLocal, BigDecimal TOKENETHaskLocal, BigDecimal TOKENETHaskVolLocal) {

        if (StorageConstant.ETHBTCbidVol.compareTo(BigDecimal.ZERO) <= 0 || StorageConstant.ETHBTCaskVol.compareTo(BigDecimal.ZERO) <= 0)
            return;

        if (TOKENBTCaskLocal.compareTo(BigDecimal.ZERO) <= 0 || TOKENBTCaskVolLocal.compareTo(BigDecimal.ZERO) <= 0 || TOKENETHbidLocal.compareTo(BigDecimal.ZERO) <= 0 || TOKENETHbidVolLocal.compareTo(BigDecimal.ZERO) <= 0)
            return;

        long timeStamp = StorageConstant.ETHBTCTime;
        MonitorUtil.checkRoute("ETH", StorageConstant.ETHBTCbid, TradeEnum.BID, StorageConstant.ETHBTCbidVol,
                "BTC", TOKENBTCaskLocal, TradeEnum.ASK, TOKENBTCaskVolLocal,
                tokenName, TOKENETHbidLocal, TradeEnum.BID, TOKENETHbidVolLocal, timeStamp);

        MonitorUtil.checkRoute("ETH", TOKENETHaskLocal, TradeEnum.ASK, TOKENETHaskVolLocal,
                tokenName, TOKENBTCbidLocal, TradeEnum.BID, TOKENBTCbidVolLocal,
                "BTC", StorageConstant.ETHBTCask, TradeEnum.ASK, StorageConstant.ETHBTCaskVol, timeStamp);

        MonitorUtil.checkRoute("BTC", TOKENBTCaskLocal, TradeEnum.ASK, TOKENBTCaskVolLocal,
                tokenName, TOKENETHbidLocal, TradeEnum.BID, TOKENETHbidVolLocal,
                "ETH", StorageConstant.ETHBTCbid, TradeEnum.BID, StorageConstant.ETHBTCbidVol, timeStamp);

        MonitorUtil.checkRoute("BTC", StorageConstant.ETHBTCask, TradeEnum.ASK, StorageConstant.ETHBTCaskVol,
                "ETH", TOKENETHaskLocal, TradeEnum.ASK, TOKENETHaskVolLocal,
                tokenName, TOKENBTCbidLocal, TradeEnum.BID, TOKENBTCbidVolLocal, timeStamp);

        MonitorUtil.checkRoute(tokenName, TOKENETHbidLocal, TradeEnum.BID, TOKENETHbidVolLocal,
                "ETH", StorageConstant.ETHBTCbid, TradeEnum.BID, StorageConstant.ETHBTCbidVol,
                "BTC", TOKENBTCaskLocal, TradeEnum.ASK, TOKENBTCaskVolLocal, timeStamp);

        MonitorUtil.checkRoute(tokenName, TOKENBTCbidLocal, TradeEnum.BID, TOKENBTCbidVolLocal,
                "BTC", StorageConstant.ETHBTCask, TradeEnum.ASK, StorageConstant.ETHBTCaskVol,
                "ETH", TOKENETHaskLocal, TradeEnum.ASK, TOKENETHaskLocal, timeStamp);

    }

    public static void checkRoute(String A, BigDecimal pA, TradeEnum type1, BigDecimal vA,
                                  String B, BigDecimal pB, TradeEnum type2, BigDecimal vB,
                                  String C, BigDecimal pC, TradeEnum type3, BigDecimal vC,
                                  long timeStamp) {

        //calculate through the trade, diminish previous trade volumes if volume bottlenecks are found
        //only do this calculation if the 1.0f test works, saving processing power
        BigDecimal amountA = (type1.equals(TradeEnum.BID) ? vA : vA.multiply(pA));

        BigDecimal amountB = (type1.equals(TradeEnum.BID) ? amountA.multiply(pA) : vA);

        //find a ratio between possible trade size for next step and what current amount is
        BigDecimal volRatioBC = (type2.equals(TradeEnum.ASK) ? vB.multiply(pB) : vB).divide(amountB, 8, BigDecimal.ROUND_HALF_EVEN);

        BigDecimal amountC;
        if (volRatioBC.compareTo(BigDecimal.ONE) < 0) {
            //reduce the size of the entire trade based on volume bottleneck
            amountA = amountA.multiply(volRatioBC);
            amountB = amountB.multiply(volRatioBC);
            amountC = (type2.equals(TradeEnum.BID) ? vB.multiply(pB) : vB);
        } else {
            amountC = (type2.equals(TradeEnum.BID) ? amountB.multiply(pB) : amountB.divide(pB, 3, BigDecimal.ROUND_HALF_EVEN));
        }

        if (amountC.compareTo(BigDecimal.ZERO) <= 0)
            return;

        BigDecimal volRatioCA = (type3.equals(TradeEnum.ASK) ? vC.multiply(pC) : vC).divide(amountC, 3, BigDecimal.ROUND_HALF_EVEN);

        BigDecimal amountAFinal;
        if (volRatioCA.compareTo(BigDecimal.ONE) < 0) {
            amountA = amountA.multiply(volRatioCA);
            amountB = amountB.multiply(volRatioCA);
            amountC = amountC.multiply(volRatioCA);
            amountAFinal = (type3.equals(TradeEnum.BID) ? vC.multiply(pC) : vC);
        } else {
            amountAFinal = (type3.equals(TradeEnum.BID) ? amountC.multiply(pC) : amountC.divide(pC, 3, BigDecimal.ROUND_HALF_EVEN));
        }


        if (A.equals("BTC") && B.equals("ETH")) {
            // BTC -> ETH 保留3位小数
            amountB = amountB.setScale(3, BigDecimal.ROUND_HALF_EVEN);
            amountA = amountB.divide(pA, 3, BigDecimal.ROUND_HALF_EVEN);
            // ETH -> TOKEN  保留2位小数
            amountC = amountC.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            // TOKEN -> BTC 保留0位小数
            amountAFinal = amountAFinal.setScale(0, BigDecimal.ROUND_HALF_EVEN);
        } else if (A.equals("BTC") && C.equals("ETH")) {
            // BTC -> TOKEN 保留0位小数
            amountB = amountB.setScale(0, BigDecimal.ROUND_HALF_EVEN);
            amountA = amountB.divide(pA, 0, BigDecimal.ROUND_HALF_EVEN);
            // TOKEN -> ETH  保留2位小数
            amountC = amountC.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            // ETH -> BTC 保留3位小数
            amountAFinal = amountAFinal.setScale(3, BigDecimal.ROUND_HALF_EVEN);
        } else if (A.equals("ETH") && B.equals("BTC")) {
            // ETH -> BTC 保留3位小数
            amountB = amountB.setScale(3, BigDecimal.ROUND_HALF_EVEN);
            amountA = amountB.divide(pA, 3, BigDecimal.ROUND_HALF_EVEN);
            // BTC -> TOKEN 保留0位小数
            amountC = amountC.setScale(0, BigDecimal.ROUND_HALF_EVEN);
            // TOKEN -> ETH  保留2位小数
            amountAFinal = amountAFinal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        } else if (A.equals("ETH") && C.equals("BTC")) {
            // ETH -> TOKEN  保留2位小数
            amountB = amountB.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            amountA = amountB.divide(pA, 2, BigDecimal.ROUND_HALF_EVEN);
            // TOKEN -> BTC 保留0位小数
            amountC = amountC.setScale(0, BigDecimal.ROUND_HALF_EVEN);
            // BTC -> ETH 保留3位小数
            amountAFinal = amountAFinal.setScale(3, BigDecimal.ROUND_HALF_EVEN);
        } else if (B.equals("ETH") && C.equals("BTC")) {
            // TOKEN -> ETH  保留2位小数
            amountB = amountB.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            amountA = amountB.divide(pA, 2, BigDecimal.ROUND_HALF_EVEN);
            // ETH -> BTC 保留3位小数
            amountC = amountC.setScale(3, BigDecimal.ROUND_HALF_EVEN);
            // BTC -> TOKEN 保留0位小数
            amountAFinal = amountAFinal.setScale(0, BigDecimal.ROUND_HALF_EVEN);
        } else if (B.equals("BTC") && C.equals("ETH")) {
            // TOKEN -> BTC 保留0位小数
            amountB = amountB.setScale(0, BigDecimal.ROUND_HALF_EVEN);
            amountA = amountB.divide(pA, 0, BigDecimal.ROUND_HALF_EVEN);
            // BTC -> ETH 保留3位小数
            amountC = amountC.setScale(3, BigDecimal.ROUND_HALF_EVEN);
            // ETH -> TOKEN  保留2位小数
            amountAFinal = amountAFinal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        } else {
            LOGGER.info("无效的交易对，退出。");
            return;
        }

        if (amountA.compareTo(BigDecimal.ZERO) > 0 && amountAFinal.compareTo(BigDecimal.ZERO) > 0 && amountAFinal.compareTo(amountA) > 0) {
            BigDecimal fee = amountA.multiply(new BigDecimal("0.0015"));//手续费费率
            BigDecimal income = amountAFinal.subtract(amountA).subtract(fee);
            if ((amountA.add(fee)).compareTo(amountAFinal) > 0) {
                LOGGER.info("伪套利机会: " + amountA + " to " + amountAFinal + " in " + A + "亏损收益: " + income);
                return;
            } else if (income.compareTo(fee.multiply(new BigDecimal(2))) < 0) {
                LOGGER.info("收益过低，手续费：" + fee + "，收益：" + income);
                return;
            } else {
                boolean isMinAmount = true;
                if (A.equals("BTC") && B.equals("ETH")) {
                    if (amountA.compareTo(xx1) < 0 || amountB.compareTo(x1) < 0 || amountAFinal.compareTo(xx1) < 0) {
                        isMinAmount = false;
                    }
                } else if (A.equals("BTC") && C.equals("ETH")) {
                    if (amountA.compareTo(xx1) < 0 || amountC.compareTo(x1) < 0 || amountAFinal.compareTo(xx1) < 0) {
                        isMinAmount = false;
                    }
                } else if (A.equals("ETH") && B.equals("BTC")) {
                    if (amountA.compareTo(x1) < 0 || amountB.compareTo(xx1) < 0 || amountAFinal.compareTo(x1) < 0) {
                        isMinAmount = false;
                    }
                } else if (A.equals("ETH") && C.equals("BTC")) {
                    if (amountA.compareTo(x1) < 0 || amountC.compareTo(xx1) < 0 || amountAFinal.compareTo(x1) < 0) {
                        isMinAmount = false;
                    }
                } else if (B.equals("ETH") && C.equals("BTC")) {
                    if (amountB.compareTo(x1) < 0 || amountC.compareTo(xx1) < 0 || amountAFinal.compareTo(xx1) < 0) {
                        isMinAmount = false;
                    }
                } else if (B.equals("BTC") && C.equals("ETH")) {
                    if (amountB.compareTo(xx1) < 0 || amountC.compareTo(x1) < 0 || amountAFinal.compareTo(x1) < 0) {
                        isMinAmount = false;
                    }
                }

                if (!isMinAmount) {
                    LOGGER.info("失败：交易" + amountA + A + "->" + amountB + B + "->" + amountC + C + "->" + amountAFinal + A + ",不满足最低购买金额，套利失败！");
                    return;
                }


                LOGGER.info("套利机会: " + amountA + " to " + amountAFinal + " in " + A);
                LOGGER.info("交易轨迹" + amountA + A + "->" + amountB + B + "->" + amountC + C + "->" + amountAFinal + A);
                LOGGER.info("交易价格" + pA + A + "->" + pB + B + "->" + pC + C);
                LOGGER.info("手续费:" + fee + A);

                BigDecimal realAmoutB = type1.equals(TradeEnum.BID) ? amountA.multiply(pA) : amountA.divide(pA, 8, BigDecimal.ROUND_HALF_EVEN);
                BigDecimal amoutBDiff = realAmoutB.subtract(amountB);
                LOGGER.info("第一次交易差额" + amoutBDiff + B);

                BigDecimal realAmoutC = type2.equals(TradeEnum.BID) ? amountB.multiply(pB) : amountB.divide(pB, 8, BigDecimal.ROUND_HALF_EVEN);
                BigDecimal realAmoutCDiff = type2.equals(TradeEnum.BID) ? amoutBDiff.multiply(pB) : amoutBDiff.divide(pB, 8, BigDecimal.ROUND_HALF_EVEN);
                BigDecimal amoutCDiff = realAmoutC.subtract(amountC);
                LOGGER.info("第二次交易差额" + amoutCDiff + C);
                amoutCDiff = realAmoutCDiff.add(amoutCDiff);
                LOGGER.info("第二次与第一次差额累加后交易差额" + amoutCDiff + C);


                BigDecimal realAmoutA = type3.equals(TradeEnum.BID) ? amountC.multiply(pC) : amountC.divide(pC, 8, BigDecimal.ROUND_HALF_EVEN);
                BigDecimal realAmoutADiff = type3.equals(TradeEnum.BID) ? amoutCDiff.multiply(pC) : amoutCDiff.divide(pC, 8, BigDecimal.ROUND_HALF_EVEN);
                BigDecimal amoutADiff = realAmoutA.subtract(amountAFinal);
                LOGGER.info("第三次交易差额" + amoutADiff + A);
                amoutADiff = realAmoutADiff.add(amoutADiff);
                LOGGER.info("第三次与前两次差额累加后交易差额" + amoutADiff + A);
                amoutADiff = amoutADiff.subtract(fee);
                LOGGER.info("预期获利: " + income + A + "===实际收益: " + amoutADiff + A);

                if (amoutADiff.compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                LOGGER.warn("=================实际收益: " + amoutADiff + A);
                BigDecimal BTCINCOME = new BigDecimal(0);
                BigDecimal ETHINCOME = new BigDecimal(0);
                BigDecimal TOKENINCOME = new BigDecimal(0);

                if (A.equals("BTC")) {
                    BTCINCOME = BTCINCOME.add(amoutADiff);
                } else if (A.equals("ETH")) {
                    ETHINCOME = ETHINCOME.add(amoutADiff);
                } else {
                    TOKENINCOME = TOKENINCOME.add(amoutADiff);
                }

                LOGGER.info("交易" + type1 + A + "->" + B + ": " + amountA + A + " to " + amountB + B + " 价格： " + pA);
                LOGGER.info("交易" + type2 + B + "->" + C + ": " + amountB + B + " to " + amountC + C + " 价格： " + pB);
                LOGGER.info("交易" + type3 + C + "->" + A + ": " + amountC + C + " to " + amountAFinal + A + " 价格： " + pC);

                synchronized (isTrading) {
                    if (isTrading) {
                        LOGGER.info("正在交易，跳出");
                        return;
                    }
                    isTrading = true;
                }

                if (type1.equals(TradeEnum.BID)) {
                    LOGGER.info("交易对：" + A + B);
                    client.newOrder(limitSell(A + B, TimeInForce.GTC, amountA.toString(), pA.toString()), response -> LOGGER.info("sell order success！"));
                } else {
                    LOGGER.info("交易对：" + B + A);
                    client.newOrder(limitBuy(B + A, TimeInForce.GTC, amountB.toString(), pA.toString()), response -> LOGGER.info("buy order success!"));
                }


                if (type2.equals(TradeEnum.BID)) {
                    LOGGER.info("交易对：" + B + C);
                    client.newOrder(limitSell(B + C, TimeInForce.GTC, amountB.toString(), pB.toString()), response -> LOGGER.info("sell order success！"));
                } else {
                    LOGGER.info("交易对：" + C + B);
                    client.newOrder(limitBuy(C + B, TimeInForce.GTC, amountC.toString(), pB.toString()), response -> LOGGER.info("buy order success!"));
                }


                if (type3.equals(TradeEnum.BID)) {
                    LOGGER.info("交易对：" + C + A);
                    client.newOrder(limitSell(C + A, TimeInForce.GTC, amountC.toString(), pC.toString()), response -> LOGGER.info("sell order success！"));
                } else {
                    LOGGER.info("交易对：" + A + C);
                    client.newOrder(limitBuy(A + C, TimeInForce.GTC, amountAFinal.toString(), pC.toString()), response -> LOGGER.info("buy order success!"));
                }


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                LOGGER.info("发生时间：" + sdf.format(new Date(timeStamp)) + "\n");
                LOGGER.warn("收益汇总，BTCINCOME：" + BTCINCOME + " | ETHINCOME：" + ETHINCOME + " | " + "TOKENINCOME：" + TOKENINCOME);


                try {
                    //暂停3秒
                    Thread.sleep(30000l);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (isTrading) {
                    System.exit(0);
                    isTrading = false;
                }

            }


            //for testing differences
//				LOGGER.info("BNBBTCbid" + StorageConstant.BNBBTCbid);
//				LOGGER.info("BNBBTCbidVol" + StorageConstant.BNBBTCbidVol);
//				LOGGER.info("BNBBTCask" + StorageConstant.BNBBTCask);
//				LOGGER.info("BNBBTCaskVol" + StorageConstant.BNBBTCaskVol);
//
//				LOGGER.info("BNBETHbid" + StorageConstant.BNBETHbid);
//				LOGGER.info("BNBETHbidVol" + StorageConstant.BNBETHbidVol);
//				LOGGER.info("BNBETHask" + StorageConstant.BNBETHask);
//				LOGGER.info("BNBETHaskVol" + StorageConstant.BNBETHaskVol);
//
//				LOGGER.info("ETHBTCbid" + StorageConstant.ETHBTCbid);
//				LOGGER.info("ETHBTCbidVol" + StorageConstant.ETHBTCbidVol);
//				LOGGER.info("ETHBTCask" + StorageConstant.ETHBTCask);
//				LOGGER.info("ETHBTCaskVol" + StorageConstant.ETHBTCaskVol);
        } else {
//                if (amountA.compareTo(BigDecimal.ZERO) > 0)
//                    LOGGER.info(A + "->" + B + "->" + C + "->" + A + " losing yield:" + (amountAFinal.divide(amountA, 6, BigDecimal.ROUND_HALF_EVEN)));
        }

    }

}
