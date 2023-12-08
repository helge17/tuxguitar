package org.herac.tuxguitar.android.variables;

import android.os.Environment;

public class TGVarEnvExternalStorageDirectory {
	
	public static final String NAME = "externalStorageDirectory";
	
	public TGVarEnvExternalStorageDirectory() {
		super();
	}
	
	public String toString() {
		return Environment.getExternalStorageDirectory().getPath();
	}
}
