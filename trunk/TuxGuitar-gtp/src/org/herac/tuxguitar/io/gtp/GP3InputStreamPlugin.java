package org.herac.tuxguitar.io.gtp;

public class GP3InputStreamPlugin extends GTPInputStreamPlugin{

	public GP3InputStreamPlugin() {
		super(new GP3InputStream(GTPSettingsUtil.instance().getSettings()));
	}
}
