package com.fil.shauni;

import net.sf.ehcache.CacheManager;

/**
 *
 * @author Filippo
 */
public class ShauniCache {

    private static CacheManager cacheManager;

    private ShauniCache() {
    }

    public static CacheManager getInstance() {
        if (cacheManager == null) {
            cacheManager = CacheManager.create("src/main/resources/ehcache.xml");
        }
        return cacheManager;
    }

    public static void shutdown() {
        if (cacheManager != null) {
            cacheManager.shutdown();
            cacheManager = null;
        }
    }
}
