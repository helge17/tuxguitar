package org.herac.tuxguitar.util.singleton;

import org.herac.tuxguitar.util.TGContext;

public interface TGSingletonFactory {
	
	Object createInstance(TGContext context);
}
