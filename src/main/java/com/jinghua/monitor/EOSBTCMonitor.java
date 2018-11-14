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
 * @version $Id: EOSBTCMonitor.java, v0.1 2018/11/13 14:04 jinghua Exp $$
 */
@ClientEndpoint
public class EOSBTCMonitor extends AbstractMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EOSBTCMonitor.class);

    @OnMessage
    public void onMessage(String message) throws JSONException {
        doMessage(message);
        StorageConstant.EOSBTCSS = true;
        LOGGER.info("EOSBTC bid: " + StorageConstant.EOSBTCbid + " " + StorageConstant.EOSBTCbidVol + " ask: " + StorageConstant.EOSBTCask + " " + StorageConstant.EOSBTCaskVol);
        if (StorageConstant.ETHBTCSS && StorageConstant.EOSBTCSS && StorageConstant.EOSETHSS) {
            DetectArbitrage.doDetectArbitrage("EOS");
        }

    }

    @Override
    protected void setBid(BigDecimal bidPrice, BigDecimal bidVol, long time) {
        StorageConstant.EOSBTCbid = bidPrice;
        StorageConstant.EOSBTCbidVol = bidVol;
        StorageConstant.EOSBTCTime = time;
    }

    @Override
    protected void setAsk(BigDecimal askPrice, BigDecimal askVol) {
        StorageConstant.EOSBTCask = askPrice;
        StorageConstant.EOSBTCaskVol = askVol;
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
        StorageConstant.EOSBTCSS = false;
        ScalperApplication.messageLatch.countDown();
    }
}
