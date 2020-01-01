package cn.objectspace.daemon.pojo.singletonbean;

import com.google.gson.Gson;

public class GsonSingleton {
	private volatile static Gson gson;
	private GsonSingleton() {}
	public static Gson getSingleton() {
		if(gson==null) {
			synchronized (GsonSingleton.class) {
				if(gson==null) {
					gson = new Gson();
				}
			}
		}
		return gson;
	}
}
