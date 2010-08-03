package org.herac.tuxguitar.app.system.plugins.base;

import org.herac.tuxguitar.app.system.plugins.TGPlugin;

public abstract class TGPluginAdapter implements TGPlugin{
	
	public String getName() {
		return "Untitled Plugin";
	}
	
	public String getAuthor() {
		return "Unavailable";
	}
	
	public String getDescription() {
		return "Unavailable";
	}
	
	public String getVersion() {
		return "Unavailable";
	}
}
