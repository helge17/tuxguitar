package org.herac.tuxguitar.util.singleton;

import org.herac.tuxguitar.util.TGContext;

public class TGSingletonUtil {
	
	public static <T> T getInstance(TGContext context, String key, TGSingletonFactory<T> factory) {
		synchronized (TGSingletonUtil.getSingletonLock(context, key)) {
			if( context.hasAttribute(key) ) {
				return context.getAttribute(key);
			}
			context.setAttribute(key, factory.createInstance(context));
			
			return getInstance(context, key, factory);
		}
	}
	
	private static Object getSingletonLock(TGContext context, String key) {
		synchronized (TGSingletonUtil.class) {
			String contextKey = (TGSingletonUtil.class.getName() + "-" + key);
			
			if( context.hasAttribute(contextKey) ) {
				return context.getAttribute(contextKey);
			}
			context.setAttribute(contextKey, new Object());
			
			return getSingletonLock(context, key);
		}
	}
}
