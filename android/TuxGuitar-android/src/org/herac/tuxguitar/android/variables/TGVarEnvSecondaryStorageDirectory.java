package org.herac.tuxguitar.android.variables;

import java.io.File;

public class TGVarEnvSecondaryStorageDirectory {
	
	private static final String[] ENVIRONMENT_VARIABLES = {"SECONDARY_STORAGE", "EXTERNAL_SDCARD_STORAGE"};
	
	public static final String NAME = "secondaryStorageDirectory";
	
	public TGVarEnvSecondaryStorageDirectory() {
		super();
	}
	
	public String toString() {
		return findSecondaryStorage();
	}
	
	public String findSecondaryStorage() {
		for(String environmentVariable : ENVIRONMENT_VARIABLES) {
			String storagePath = System.getenv(environmentVariable);
			if (storagePath != null) {
				String[] storagePaths = storagePath.split(File.pathSeparator);
				if( storagePaths != null ) {
					for(String currentStoragePath : storagePaths) {
						File file = new File(currentStoragePath);
						if( file.exists() && file.canRead() ) {
							return file.getAbsolutePath();
						}
					}
				}
			}
		}
		return null;
	}
}
