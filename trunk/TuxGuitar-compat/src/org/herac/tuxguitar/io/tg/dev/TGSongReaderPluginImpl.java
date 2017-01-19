package org.herac.tuxguitar.io.tg.dev;

import org.herac.tuxguitar.io.tg.TGFileFormatDetectorImpl;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;
import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.tg.TGAbstractSongReaderPlugin;

public class TGSongReaderPluginImpl extends TGAbstractSongReaderPlugin{
	
	protected TGSongReader createInputStream(TGContext context) throws TGPluginException {
		return new TGSongReaderImpl();
	}
	
	protected TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return new TGFileFormatDetectorImpl(TGSongReaderImpl.SUPPORTED_FORMAT);
	}
}
