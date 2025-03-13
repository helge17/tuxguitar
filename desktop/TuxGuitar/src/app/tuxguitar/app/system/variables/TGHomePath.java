package app.tuxguitar.app.system.variables;

import app.tuxguitar.app.util.TGFileUtils;

public class TGHomePath {

	public static final String NAME = "tuxguitar.home.path";

	public TGHomePath() {
		super();
	}

	public String toString() {
		return TGFileUtils.PATH_HOME;
	}
}
