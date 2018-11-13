/**
 * BBD Service Inc
 * All Rights Reserved @2018
 */
package com.jinghua.monitor;

import com.jinghua.constants.StorageConstant;
import com.jinghua.utils.MonitorUtil;

/**
 * @author jinghua
 * @version $Id: DetectArbitrage.java, v0.1 2018/11/13 13:56 jinghua Exp $$
 */
public class DetectArbitrage {

    static public void doDetectArbitrage(String tokenName) {
        switch (tokenName) {
            case "EOS":
                MonitorUtil.doRoute(tokenName,
                        StorageConstant.EOSBTCbid, StorageConstant.EOSBTCbidVol, StorageConstant.EOSBTCask, StorageConstant.EOSBTCaskVol,
                        StorageConstant.EOSETHbid, StorageConstant.EOSETHbidVol, StorageConstant.EOSETHask, StorageConstant.EOSETHaskVol);
                break;
            case "BNB":
                MonitorUtil.doRoute(tokenName,
                        StorageConstant.BNBBTCbid, StorageConstant.BNBBTCbidVol, StorageConstant.BNBBTCask, StorageConstant.BNBBTCaskVol,
                        StorageConstant.BNBETHbid, StorageConstant.BNBETHbidVol, StorageConstant.BNBETHask, StorageConstant.BNBETHaskVol);
                break;
        }

    }
}
