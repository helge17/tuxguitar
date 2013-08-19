package org.herac.tuxguitar.io.gtp;

public class GP4InputStreamPlugin extends GTPInputStreamPlugin{

	public GP4InputStreamPlugin() {
		super(new GP4InputStream(GTPSettingsUtil.instance().getSettings()));
	}
}
