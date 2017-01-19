package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.io.base.TGFileFormat;

public class ABCFileFormat {
	
	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("Abc", "text/x-abc", new String[]{"abc"});
	
	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}
}
