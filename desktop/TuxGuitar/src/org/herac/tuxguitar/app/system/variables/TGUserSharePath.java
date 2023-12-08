package org.herac.tuxguitar.app.system.variables;

import org.herac.tuxguitar.app.util.TGFileUtils;

public class TGUserSharePath {
	
	public static final String NAME = "tuxguitar.user-share.path";
	
	public TGUserSharePath() {
		super();
	}
	
	public String toString() {
		return TGFileUtils.PATH_USER_SHARE_PATH;
	}
}
