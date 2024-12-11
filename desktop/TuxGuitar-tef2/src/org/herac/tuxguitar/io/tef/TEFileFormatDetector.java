package org.herac.tuxguitar.io.tef2;

import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatDetector;

public class TEFileFormatDetector implements TGFileFormatDetector {
	
	public TEFileFormatDetector() {
		super();
	}
	
	public TGFileFormat getFileFormat(InputStream is) {		
		try {
            byte[] versionBytes = is.readNBytes(5);
            
            // File is not long enough to read version metadata.
            if (versionBytes.length != 5)
            {
                return null;
            }
            
            // From TEF file header.
            // First line is song title, then author, then author, then comments.
            // There is no version metadata, so this is a check AGAINST version 3.
			if( this.isSupportedVersion(versionBytes[3], versionBytes[2], versionBytes[4])) {
				return TESongReader.FILE_FORMAT;
			}
			return null;
		} catch (Throwable throwable) {
			return null;
		}
	}

    public boolean isSupportedVersion(int majorVersion, int minorVersion, int revision) {
        return majorVersion != 3;
    }
}
