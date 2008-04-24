package org.herac.tuxguitar.gui.system.plugins;

public interface TGPlugin {
	
	public void init() throws TGPluginException;
	
	public void close() throws TGPluginException;
	
	public void setEnabled(boolean enabled) throws TGPluginException;
	
	public String getName();
	
	public String getDescription();
	
	public String getVersion();
	
	public String getAuthor();
	
}
