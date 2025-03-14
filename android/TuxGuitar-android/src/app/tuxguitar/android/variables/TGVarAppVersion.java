package app.tuxguitar.android.variables;

import app.tuxguitar.util.TGVersion;

public class TGVarAppVersion {

	public static final String NAME = "appversion";

	public String toString() {
		return TGVersion.CURRENT.getVersion();
	}
}
