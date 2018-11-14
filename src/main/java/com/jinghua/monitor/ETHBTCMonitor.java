/**
 * BBD Service Inc
 * All Rights Reserved @2018
 */
package com.jinghua.monitor;

import com.jinghua.ScalperApplication;
import com.jinghua.constants.StorageConstant;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import java.math.BigDecimal;

/**
 * @author jinghua
 * @version $Id: ETHBTCMonitor.java, v0.1 2018/11/13 14:07 jinghua Exp $$
 */
@ClientEndpoint
public class ETHBTCMonitor extends AbstractMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ETHBTCMonitor.class);

    @OnMessage
    public void onMessage(String message) throws JSONException {
        doMessage(message);
        StorageConstant.ETHBTCSS = true;
        LOGGER.info("ETHBTC bid: " + StorageConstant.ETHBTCbid + " " + StorageConstant.ETHBTCbidVol + " ask: " + StorageConstant.ETHBTCask + " " + StorageConstant.ETHBTCaskVol);
        if (StorageConstant.ETHBTCSS && StorageConstant.BNBBTCSS && StorageConstant.BNBETHSS) {
            DetectArbitrage.doDetectArbitrage("BNB");
        }
        if (StorageConstant.ETHBTCSS && StorageConstant.EOSBTCSS && StorageConstant.EOSETHSS) {
            DetectArbitrage.doDetectArbitrage("EOS");
        }
    }

    @Override
    protected void setBid(BigDecimal bidPrice, BigDecimal bidVol, long time) {
        StorageConstant.ETHBTCbid = bidPrice;
        StorageConstant.ETHBTCbidVol = bidVol;
        StorageConstant.ETHBTCTime = time;
    }

    @Override
    protected void setAsk(BigDecimal askPrice, BigDecimal askVol) {
        StorageConstant.ETHBTCask = askPrice;
        StorageConstant.ETHBTCaskVol = askVol;
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
        StorageConstant.ETHBTCSS = false;
        ScalperApplication.messageLatch.countDown();
    }
}
