package com.ij34.server.msg;

import java.util.concurrent.atomic.AtomicInteger;

public class PackageIdGenerator {

    private static final AtomicInteger PACKETIDS = new AtomicInteger();

    // 默认自增1
    public static int generator() {
        return PACKETIDS.incrementAndGet();
    }

}
