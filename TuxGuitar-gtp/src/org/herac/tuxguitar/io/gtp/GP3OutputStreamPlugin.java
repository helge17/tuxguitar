package org.herac.tuxguitar.io.gtp;

public class GP3OutputStreamPlugin extends GTPOutputStreamPlugin{

	public GP3OutputStreamPlugin() {
		super(new GP3OutputStream(GTPSettingsUtil.instance().getSettings()));
	}
}
