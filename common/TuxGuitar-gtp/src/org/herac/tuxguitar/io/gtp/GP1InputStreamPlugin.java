package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP1InputStreamPlugin extends GTPInputStreamPlugin{

	public GP1InputStreamPlugin() {
		super();
	}
	
	protected TGSongReader createInputStream(TGContext context) throws TGPluginException {
		return new GP1InputStream(GTPSettingsManager.getInstance(context).getSettings());
	}

	public TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return new GTPFileFormatDetector(GP1InputStream.SUPPORTED_VERSIONS);
	}
}
