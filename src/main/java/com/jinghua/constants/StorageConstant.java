/**
 * BBD Service Inc
 * All Rights Reserved @2018
 */
package com.jinghua.constants;

import java.math.BigDecimal;

/**
 * @author jinghua
 * @version $Id: StorageConstant.java, v0.1 2018/11/13 11:38 jinghua Exp $$
 */
public class StorageConstant {

    public static volatile boolean ETHBTCSS = false;
    public static volatile boolean EOSBTCSS = false;
    public static volatile boolean EOSETHSS = false;
    public static volatile boolean BNBBTCSS = false;
    public static volatile boolean BNBETHSS = false;

    //ETH/BTC
    public static volatile long ETHBTCTime = 0;
    public static volatile BigDecimal ETHBTCbid = BigDecimal.ZERO;
    public static volatile BigDecimal ETHBTCbidVol = BigDecimal.ZERO;

    public static volatile BigDecimal ETHBTCask = BigDecimal.ZERO;
    public static volatile BigDecimal ETHBTCaskVol = BigDecimal.ZERO;

    //EOS/BTC
    public static volatile long EOSBTCTime = 0;
    public static volatile BigDecimal EOSBTCbid = BigDecimal.ZERO;
    public static volatile BigDecimal EOSBTCbidVol = BigDecimal.ZERO;

    public static volatile BigDecimal EOSBTCask = BigDecimal.ZERO;
    public static volatile BigDecimal EOSBTCaskVol = BigDecimal.ZERO;

    //EOS/ETH
    public static volatile long EOSETHTime = 0;
    public static volatile BigDecimal EOSETHbid = BigDecimal.ZERO;
    public static volatile BigDecimal EOSETHbidVol = BigDecimal.ZERO;

    public static volatile BigDecimal EOSETHask = BigDecimal.ZERO;
    public static volatile BigDecimal EOSETHaskVol = BigDecimal.ZERO;

    //BNB/BTC
    public static volatile long BNBBTCTime = 0;
    public static volatile BigDecimal BNBBTCbid = BigDecimal.ZERO;
    public static volatile BigDecimal BNBBTCbidVol = BigDecimal.ZERO;

    public static volatile BigDecimal BNBBTCask = BigDecimal.ZERO;
    public static volatile BigDecimal BNBBTCaskVol = BigDecimal.ZERO;

    //BNB/ETH
    public static volatile long BNBETHTime = 0;
    public static volatile BigDecimal BNBETHbid = BigDecimal.ZERO;
    public static volatile BigDecimal BNBETHbidVol = BigDecimal.ZERO;

    public static volatile BigDecimal BNBETHask = BigDecimal.ZERO;
    public static volatile BigDecimal BNBETHaskVol = BigDecimal.ZERO;
}
