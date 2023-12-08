package org.herac.tuxguitar.app.system.variables;

import org.herac.tuxguitar.util.TGVersion;

public class TGVarAppVersion {
	
	public static final String NAME = "appversion";
	
	public String toString() {
		return TGVersion.CURRENT.getVersion();
	}
}
