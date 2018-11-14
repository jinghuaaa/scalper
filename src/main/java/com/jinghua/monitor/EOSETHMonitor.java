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
 * @version $Id: EOSETHMonitor.java, v0.1 2018/11/13 14:05 jinghua Exp $$
 */
@ClientEndpoint
public class EOSETHMonitor extends AbstractMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EOSETHMonitor.class);

    @OnMessage
    public void onMessage(String message) throws JSONException {
        doMessage(message);
        StorageConstant.EOSETHSS = true;
        LOGGER.info("EOSETH bid: " + StorageConstant.EOSETHbid + " " + StorageConstant.EOSETHbidVol + " ask: " + StorageConstant.EOSETHask + " " + StorageConstant.EOSETHaskVol);
        if (StorageConstant.ETHBTCSS && StorageConstant.EOSBTCSS && StorageConstant.EOSETHSS) {
            DetectArbitrage.doDetectArbitrage("EOS");
        }
    }

    @Override
    protected void setBid(BigDecimal bidPrice, BigDecimal bidVol, long time) {
        StorageConstant.EOSETHbid = bidPrice;
        StorageConstant.EOSETHbidVol = bidVol;
        StorageConstant.EOSETHTime = time;
    }

    @Override
    protected void setAsk(BigDecimal askPrice, BigDecimal askVol) {
        StorageConstant.EOSETHask = askPrice;
        StorageConstant.EOSETHaskVol = askVol;
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
        StorageConstant.EOSETHSS = false;
        ScalperApplication.messageLatch.countDown();
    }
}
