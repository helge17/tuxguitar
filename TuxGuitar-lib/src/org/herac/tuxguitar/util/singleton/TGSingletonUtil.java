package org.herac.tuxguitar.util.singleton;

import org.herac.tuxguitar.util.TGContext;

public class TGSingletonUtil {
	
	public static <T> T getInstance(TGContext context, String key, TGSingletonFactory<T> factory) {
		synchronized (TGSingletonUtil.class) {
			if( context.hasAttribute(key) ) {
				return context.getAttribute(key);
			}
			context.setAttribute(key, factory.createInstance(context));
			
			return getInstance(context, key, factory);
		}
	}
}
