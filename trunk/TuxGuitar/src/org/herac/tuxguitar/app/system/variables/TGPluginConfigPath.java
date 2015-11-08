package org.herac.tuxguitar.app.system.variables;

import org.herac.tuxguitar.app.util.TGFileUtils;

public class TGPluginConfigPath {
	
	public static final String NAME = "tuxguitar.plugin-config.path";
	
	public TGPluginConfigPath() {
		super();
	}
	
	public String toString() {
		return TGFileUtils.PATH_USER_PLUGINS_CONFIG;
	}
}
