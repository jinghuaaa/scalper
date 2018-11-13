/**
 * BBD Service Inc
 * All Rights Reserved @2018
 */
package com.jinghua.monitor;

import com.jinghua.ScalperApplication;
import com.jinghua.constants.StorageConstant;
import org.json.JSONException;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import java.math.BigDecimal;

/**
 * @author jinghua
 * @version $Id: BNBBTCMonitor.java, v0.1 2018/11/13 13:52 jinghua Exp $$
 */
@ClientEndpoint
public class BNBBTCMonitor extends AbstractMonitor {

    @OnMessage
    public void onMessage(String message) throws JSONException {
        doMessage(message);
        StorageConstant.BNBBTCSS = true;
//        System.out.println("BNBBTC bid: " +  Storage.BNBBTCbid + " " + Storage.BNBBTCbidVol+" ask: " + Storage.BNBBTCask + " " + Storage.BNBBTCaskVol);
        if (StorageConstant.ETHBTCSS && StorageConstant.BNBBTCSS && StorageConstant.BNBETHSS) {
            DetectArbitrage.doDetectArbitrage("BNB");
        }
    }

    @Override
    protected void setBid(BigDecimal bidPrice, BigDecimal bidVol, long time) {
        StorageConstant.BNBBTCbid = bidPrice;
        StorageConstant.BNBBTCbidVol = bidVol;
        StorageConstant.BNBBTCTime = time;
    }

    @Override
    protected void setAsk(BigDecimal askPrice, BigDecimal askVol) {
        StorageConstant.BNBBTCask = askPrice;
        StorageConstant.BNBBTCaskVol = askVol;
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
        StorageConstant.BNBBTCSS = false;
        ScalperApplication.messageLatch.countDown();
    }
}
