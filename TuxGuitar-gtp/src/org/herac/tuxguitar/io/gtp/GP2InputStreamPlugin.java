package org.herac.tuxguitar.io.gtp;

public class GP2InputStreamPlugin extends GTPInputStreamPlugin{

	public GP2InputStreamPlugin() {
		super(new GP2InputStream(GTPSettingsUtil.instance().getSettings()));
	}
}
