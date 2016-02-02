package org.herac.tuxguitar.android.variables;

import java.io.File;

public class TGVarEnvSecondaryStorageDirectory {
	
	private static final String ENVIRONMENT_VARIABLE = "SECONDARY_STORAGE";
	
	public static final String NAME = "secondaryStorageDirectory";
	
	public TGVarEnvSecondaryStorageDirectory() {
		super();
	}
	
	public String toString() {
		return findSecondaryStorage();
	}
	
	public String findSecondaryStorage() {
		String path = System.getenv(ENVIRONMENT_VARIABLE);
		if( path != null ) {
			File file = new File(path);
			if( file.exists() ) {
				return file.getAbsolutePath();
			}
		}
		return null;
	}
}
