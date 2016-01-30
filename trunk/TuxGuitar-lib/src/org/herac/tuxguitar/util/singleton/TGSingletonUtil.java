package org.herac.tuxguitar.util.singleton;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.util.TGContext;

public class TGSingletonUtil {
	
	private static Map<String, Object> singletonLocks;
	
	public static <T> T getInstance(TGContext context, String key, TGSingletonFactory<T> factory) {
		synchronized (TGSingletonUtil.getSingletonLock(key)) {
			if( context.hasAttribute(key) ) {
				return context.getAttribute(key);
			}
			context.setAttribute(key, factory.createInstance(context));
			
			return getInstance(context, key, factory);
		}
	}
	
	private static Object getSingletonLock(String key) {
		synchronized (TGSingletonUtil.class) {
			if( singletonLocks == null ) {
				singletonLocks = new HashMap<String, Object>();
			}
			
			if( singletonLocks.containsKey(key) ) {
				return singletonLocks.get(key);
			}
			singletonLocks.put(key, new Object());
			
			return getSingletonLock(key);
		}
	}
}
