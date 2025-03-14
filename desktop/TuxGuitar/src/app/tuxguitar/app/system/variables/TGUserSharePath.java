package app.tuxguitar.app.system.variables;

import app.tuxguitar.app.util.TGFileUtils;

public class TGUserSharePath {

	public static final String NAME = "tuxguitar.user-share.path";

	public TGUserSharePath() {
		super();
	}

	public String toString() {
		return TGFileUtils.PATH_USER_SHARE_PATH;
	}
}
