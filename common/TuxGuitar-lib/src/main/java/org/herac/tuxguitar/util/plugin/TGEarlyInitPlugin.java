package org.herac.tuxguitar.util.plugin;

import org.herac.tuxguitar.util.TGContext;

public interface TGEarlyInitPlugin extends TGPlugin {

	// called just after TGApplication creation, before UI is initialized
	void earlyInit(TGContext context) throws TGPluginException;

}
