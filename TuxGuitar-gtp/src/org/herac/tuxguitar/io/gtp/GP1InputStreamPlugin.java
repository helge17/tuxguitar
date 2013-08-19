package org.herac.tuxguitar.io.gtp;

public class GP1InputStreamPlugin extends GTPInputStreamPlugin{

	public GP1InputStreamPlugin() {
		super(new GP1InputStream(GTPSettingsUtil.instance().getSettings()));
	}
}
