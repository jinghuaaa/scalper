/**
 * BBD Service Inc
 * All Rights Reserved @2018
 */
package com.jinghua.monitor;

import java.math.BigDecimal;

/**
 * @author jinghua
 * @version $Id: AllMarketTickersMonitor.java, v0.1 2018/11/13 15:41 jinghua Exp $$
 */
public class AllMarketTickersMonitor extends AbstractMonitor {

    private static final String URIbnbbtc = "wss://stream.binance.com:9443/ws/!ticker@arr";

    @Override
    protected void setBid(BigDecimal bidPrice, BigDecimal bidVol, long time) {

    }

    @Override
    protected void setAsk(BigDecimal askPrice, BigDecimal askVol) {

    }
    
}
