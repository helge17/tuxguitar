package app.tuxguitar.io.gtp;

import app.tuxguitar.io.base.TGFileFormatDetector;
import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

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
