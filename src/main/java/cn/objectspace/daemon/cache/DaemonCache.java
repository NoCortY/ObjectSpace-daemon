package cn.objectspace.daemon.cache;

import java.util.HashMap;
import java.util.Map;

public class DaemonCache {
	public static Map<String,Object> cacheMap;
	public static Map<String,Object> microServiceCache;
	private DaemonCache() {}
	public static Map<String,Object> getCacheMap() {
		if(cacheMap==null) {
			synchronized (DaemonCache.class) {
				if(cacheMap==null) {
					cacheMap = new HashMap<String,Object>();
				}
			}
		}
		return cacheMap;
	}
	public static Map<String,Object> getMicroServiceCache(){
		if(microServiceCache==null) {
			synchronized (DaemonCache.class) {
				if(microServiceCache==null) {
					microServiceCache = new HashMap<String,Object>();
				}
			}
		}
		return microServiceCache;
	}
}
