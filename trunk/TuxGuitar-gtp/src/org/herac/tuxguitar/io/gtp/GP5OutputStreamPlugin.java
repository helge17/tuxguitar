package org.herac.tuxguitar.io.gtp;

public class GP5OutputStreamPlugin extends GTPOutputStreamPlugin{

	public GP5OutputStreamPlugin() {
		super(new GP5OutputStream(GTPSettingsUtil.instance().getSettings()));
	}
}
