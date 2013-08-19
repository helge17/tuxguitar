package org.herac.tuxguitar.io.gtp;

public class GP5InputStreamPlugin extends GTPInputStreamPlugin{

	public GP5InputStreamPlugin() {
		super(new GP5InputStream(GTPSettingsUtil.instance().getSettings()));
	}
}
