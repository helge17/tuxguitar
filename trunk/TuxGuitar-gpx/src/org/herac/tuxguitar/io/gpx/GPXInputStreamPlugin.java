package org.herac.tuxguitar.io.gpx;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.io.plugin.TGInputStreamPlugin;

public class GPXInputStreamPlugin extends TGInputStreamPlugin{
	
	public static final String MODULE_ID = "tuxguitar-gpx";
	
	protected TGInputStreamBase getInputStream() {
		return new GPXInputStream();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
