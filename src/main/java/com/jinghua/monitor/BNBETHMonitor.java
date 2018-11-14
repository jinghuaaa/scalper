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
 * @version $Id: BNBETHMonitor.java, v0.1 2018/11/13 14:01 jinghua Exp $$
 */
@ClientEndpoint
public class BNBETHMonitor extends AbstractMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BNBETHMonitor.class);

    @OnMessage
    public void onMessage(String message) throws JSONException {
        doMessage(message);
        StorageConstant.BNBETHSS = true;
        LOGGER.info("BNBETH bid: " + StorageConstant.BNBETHbid + " " + StorageConstant.BNBETHbidVol + " ask: " + StorageConstant.BNBETHask + " " + StorageConstant.BNBETHaskVol);
        if (StorageConstant.ETHBTCSS && StorageConstant.BNBBTCSS && StorageConstant.BNBETHSS) {
            DetectArbitrage.doDetectArbitrage("BNB");
        }
    }

    @Override
    protected void setBid(BigDecimal bidPrice, BigDecimal bidVol, long time) {
        StorageConstant.BNBETHbid = bidPrice;
        StorageConstant.BNBETHbidVol = bidVol;
        StorageConstant.BNBETHTime = time;
    }

    @Override
    protected void setAsk(BigDecimal askPrice, BigDecimal askVol) {
        StorageConstant.BNBETHask = askPrice;
        StorageConstant.BNBETHaskVol = askVol;
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
        StorageConstant.BNBETHSS = false;
        ScalperApplication.messageLatch.countDown();
    }
}
