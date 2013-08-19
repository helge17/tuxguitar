package org.herac.tuxguitar.io.gtp;

public class GP4OutputStreamPlugin extends GTPOutputStreamPlugin{

	public GP4OutputStreamPlugin() {
		super(new GP4OutputStream(GTPSettingsUtil.instance().getSettings()));
	}
}
