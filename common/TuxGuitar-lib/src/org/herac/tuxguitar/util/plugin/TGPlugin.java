package org.herac.tuxguitar.util.plugin;

import org.herac.tuxguitar.util.TGContext;

public interface TGPlugin {
	
	String getModuleId();
	
	void connect(TGContext context) throws TGPluginException;
	
	void disconnect(TGContext context) throws TGPluginException;
}