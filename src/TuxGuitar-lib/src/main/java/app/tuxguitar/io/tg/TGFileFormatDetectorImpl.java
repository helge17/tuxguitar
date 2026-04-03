package app.tuxguitar.io.tg;

import java.io.IOException;
import java.io.InputStream;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatDetector;
import app.tuxguitar.util.TGVersion;

public class TGFileFormatDetectorImpl extends TGStream implements TGFileFormatDetector{

	public TGFileFormatDetectorImpl() {
		super();
	}

	@Override
	public TGFileFormat getFileFormat(InputStream inputStream) {
		TGVersion version;
		try {
			version = this.getFileFormatVersion(this.getDecompressedVersion(inputStream));
		} catch (IOException e) {
			return null;
		}
		if ((version != null) && (version.getMajor() == FILE_FORMAT_TGVERSION.getMajor())) {
			return TG_FORMAT;
		}
		return null;
	}

}
