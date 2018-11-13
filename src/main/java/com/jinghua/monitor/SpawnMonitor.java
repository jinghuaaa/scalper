/**
 * BBD Service Inc
 * All Rights Reserved @2018
 */
package com.jinghua.monitor;

import org.springframework.scheduling.annotation.Async;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;

/**
 * @author jinghua
 * @version $Id: SpawnMonitor.java, v0.1 2018/11/13 14:09 jinghua Exp $$
 */
public class SpawnMonitor {

    WebSocketContainer container;
    Class monitorClass;
    URI Uri;

    public SpawnMonitor(Class monitorClass, URI Uri) {
        this.container = ContainerProvider.getWebSocketContainer();
        this.monitorClass = monitorClass;
        this.Uri = Uri;
    }

    @Async
    public void execute() {
        // TODO Auto-generated method stub
        try {
            container.connectToServer(this.monitorClass, this.Uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
