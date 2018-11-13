package com.jinghua;

import com.jinghua.monitor.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class ScalperApplication {

    private static final String URIbnbbtc = "wss://stream.binance.com:9443/ws/bnbbtc@ticker";
    private static final String URIbnbeth = "wss://stream.binance.com:9443/ws/bnbeth@ticker";
    private static final String URIethbtc = "wss://stream.binance.com:9443/ws/ethbtc@ticker";
    private static final String URIeosbtc = "wss://stream.binance.com:9443/ws/eosbtc@ticker";
    private static final String URIeoseth = "wss://stream.binance.com:9443/ws/eoseth@ticker";

    public static CountDownLatch messageLatch = new CountDownLatch(1);

    public static void main(String[] args) {
        // SpringApplication.run(ScalperApplication.class, args);
        try {
            new SpawnMonitor(ETHBTCMonitor.class, URI.create(URIethbtc)).execute();
            new SpawnMonitor(EOSBTCMonitor.class, URI.create(URIeosbtc)).execute();
            new SpawnMonitor(EOSETHMonitor.class, URI.create(URIeoseth)).execute();
            new SpawnMonitor(BNBBTCMonitor.class, URI.create(URIbnbbtc)).execute();
            new SpawnMonitor(BNBETHMonitor.class, URI.create(URIbnbeth)).execute();
            messageLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
