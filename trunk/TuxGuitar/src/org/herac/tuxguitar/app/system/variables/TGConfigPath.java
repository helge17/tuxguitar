package org.herac.tuxguitar.app.system.variables;

import org.herac.tuxguitar.app.util.TGFileUtils;

public class TGConfigPath {
	
	public static final String NAME = "tuxguitar.config.path";
	
	public TGConfigPath() {
		super();
	}
	
	public String toString() {
		return TGFileUtils.PATH_USER_CONFIG;
	}
}
