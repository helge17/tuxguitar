package org.herac.tuxguitar.io.gpx;

import org.herac.tuxguitar.app.system.plugins.base.TGInputStreamPlugin;
import org.herac.tuxguitar.io.base.TGInputStreamBase;

public class GPXInputStreamPlugin extends TGInputStreamPlugin{
	
	public static final String MODULE_ID = "tuxguitar-gpx";
	
	protected TGInputStreamBase getInputStream() {
		return new GPXInputStream();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
