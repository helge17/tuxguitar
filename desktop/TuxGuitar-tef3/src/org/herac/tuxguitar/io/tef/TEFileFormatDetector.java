package org.herac.tuxguitar.io.tef3;

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
            // 0x00 0x00 0x04 0x03 0xB1
            // 0x03 = Major Version
            // 0x04 = Minor Version
            // 0xB1 = Revision
			if( this.isSupportedVersion(versionBytes[3], versionBytes[2], versionBytes[4])) {
				return TESongReader.FILE_FORMAT;
			}
			return null;
		} catch (Throwable throwable) {
			return null;
		}
	}

    public boolean isSupportedVersion(int majorVersion, int minorVersion, int revision) {
        return majorVersion == 3;
    }
}
