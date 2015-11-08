package org.herac.tuxguitar.app.system.variables;

import org.herac.tuxguitar.app.util.TGFileUtils;

public class TGHomePath {
	
	public static final String NAME = "tuxguitar.home.path";
	
	public TGHomePath() {
		super();
	}
	
	public String toString() {
		return TGFileUtils.PATH_HOME;
	}
}
