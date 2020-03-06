package cn.objectspace.daemon.cache;

import java.util.HashMap;
import java.util.Map;

public class DaemonCache {
	private static Map<String,Object> coreMap;
	private static Map<String,Object> microServiceCache;
	private static Map<String,Long> rwCache;
	private DaemonCache() {}
	public static Map<String,Object> getCoreMap() {
		if(coreMap==null) {
			synchronized (DaemonCache.class) {
				if(coreMap==null) {
					coreMap = new HashMap<String,Object>();
				}
			}
		}
		return coreMap;
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

	public static Map<String, Long> getRwCache() {
		if(rwCache==null){
			synchronized (DaemonCache.class){
				if(rwCache==null){
					rwCache = new HashMap<>();
				}
			}
		}
		return rwCache;
	}

}
