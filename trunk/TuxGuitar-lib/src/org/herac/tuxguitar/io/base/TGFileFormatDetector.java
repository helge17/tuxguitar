package org.herac.tuxguitar.io.base;

import java.io.InputStream;

public interface TGFileFormatDetector {
	
	TGFileFormat getFileFormat(InputStream inputStream);
}
