package app.tuxguitar.io.tg.v11;

import app.tuxguitar.io.base.TGFileFormatDetector;
import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.tg.TGAbstractSongReaderPlugin;
import app.tuxguitar.io.tg.v15.TGFileFormatDetectorImpl;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class TGSongReaderPluginImpl extends TGAbstractSongReaderPlugin{

	protected TGSongReader createInputStream(TGContext context) throws TGPluginException {
		return new TGSongReaderImpl();
	}

	protected TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return new TGFileFormatDetectorImpl(TGSongReaderImpl.SUPPORTED_FORMAT);
	}
}
