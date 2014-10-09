package org.herac.tuxguitar.util.plugin;

import org.herac.tuxguitar.util.TGContext;

public interface TGPlugin {
	
	public void init(TGContext context) throws TGPluginException;
	
	public void close() throws TGPluginException;
	
	public void setEnabled(boolean enabled) throws TGPluginException;
	
	public String getModuleId();
	
}
