package org.herac.tuxguitar.android.variables;

import org.herac.tuxguitar.util.TGVersion;

public class TGVarAppVersion {
	
	public static final String NAME = "appversion";
	
	public String toString() {
		return TGVersion.CURRENT.getVersion();
	}
}
