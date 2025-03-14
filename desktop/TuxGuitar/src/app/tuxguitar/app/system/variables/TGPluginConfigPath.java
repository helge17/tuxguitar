package app.tuxguitar.app.system.variables;

import app.tuxguitar.app.util.TGFileUtils;

public class TGPluginConfigPath {

	public static final String NAME = "tuxguitar.plugin-config.path";

	public TGPluginConfigPath() {
		super();
	}

	public String toString() {
		return TGFileUtils.PATH_USER_PLUGINS_CONFIG;
	}
}
