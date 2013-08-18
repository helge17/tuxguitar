package org.herac.tuxguitar.util.plugin;

public interface TGPlugin {
	
	public void init() throws TGPluginException;
	
	public void close() throws TGPluginException;
	
	public void setEnabled(boolean enabled) throws TGPluginException;
	
	public String getModuleId();
	
}
