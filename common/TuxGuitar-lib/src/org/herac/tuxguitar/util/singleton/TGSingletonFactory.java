package org.herac.tuxguitar.util.singleton;

import org.herac.tuxguitar.util.TGContext;

public interface TGSingletonFactory<T> {
	
	T createInstance(TGContext context);
}
