package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGFileFormatDetector;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP4InputStreamPlugin extends GTPInputStreamPlugin{

	public GP4InputStreamPlugin() {
		super();
	}
	
	protected TGSongReader createInputStream(TGContext context) throws TGPluginException {
		return new GP4InputStream(GTPSettingsManager.getInstance(context).getSettings());
	}
	
	public TGFileFormatDetector createFileFormatDetector(TGContext context) throws TGPluginException {
		return new GTPFileFormatDetector(GP4InputStream.SUPPORTED_VERSIONS);
	}
}
