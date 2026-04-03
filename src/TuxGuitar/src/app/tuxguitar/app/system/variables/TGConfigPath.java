package app.tuxguitar.app.system.variables;

import app.tuxguitar.app.util.TGFileUtils;

public class TGConfigPath {

	public static final String NAME = "tuxguitar.config.path";

	public TGConfigPath() {
		super();
	}

	public String toString() {
		return TGFileUtils.PATH_USER_CONFIG;
	}
}
