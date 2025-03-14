package app.tuxguitar.io.gtp;

import app.tuxguitar.io.base.TGFileFormatDetector;
import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

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
