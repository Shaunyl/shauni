package com.fil.shauni;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 *
 * @author Filippo
 */
public class ShauniCache {

    private static CacheManager cacheManager;

//    private static final 
    private ShauniCache() {
    }

    public static CacheManager getInstance() {
        if (cacheManager == null) {
            cacheManager = CacheManager.create("/ehcache.xml");
        }
        return cacheManager;
    }

    public static void shutdown() {
        if (cacheManager != null) {
            cacheManager.shutdown();
            cacheManager = null;
        }
    }

    public static Cache getCache(String name) {
        cacheManager = getInstance();
        return cacheManager.getCache(name);
    }
}
