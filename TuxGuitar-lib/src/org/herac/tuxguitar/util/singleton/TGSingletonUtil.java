package org.herac.tuxguitar.util.singleton;

import org.herac.tuxguitar.util.TGContext;

public class TGSingletonUtil {
	
	public static Object getInstance(TGContext context, String key, TGSingletonFactory factory) {
		if( context.hasAttribute(key) ) {
			return context.getAttribute(key);
		}
		context.setAttribute(key, factory.createInstance(context));
		
		return getInstance(context, key, factory);
	}
}
