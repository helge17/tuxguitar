package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP3InputStreamPlugin extends GTPInputStreamPlugin{

	public GP3InputStreamPlugin() {
		super();
	}
	
	protected TGSongReader createInputStream(TGContext context) throws TGPluginException {
		return new GP3InputStream(GTPSettingsManager.getInstance(context).getSettings());
	}
	
	public TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return new GTPFileFormatDetector(GP3InputStream.SUPPORTED_VERSIONS);
	}
}
