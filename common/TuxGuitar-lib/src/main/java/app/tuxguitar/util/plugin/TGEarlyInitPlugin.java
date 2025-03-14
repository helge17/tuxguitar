package app.tuxguitar.util.plugin;

import app.tuxguitar.util.TGContext;

public interface TGEarlyInitPlugin extends TGPlugin {

	// called just after TGApplication creation, before UI is initialized
	void earlyInit(TGContext context) throws TGPluginException;

}
