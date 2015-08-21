package org.herac.tuxguitar.io.gpx;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.io.plugin.TGInputStreamPlugin;
import org.herac.tuxguitar.util.TGContext;

public class GPXInputStreamPlugin extends TGInputStreamPlugin{
	
	public static final String MODULE_ID = "tuxguitar-gpx";
	
	protected TGInputStreamBase createInputStream(TGContext context) {
		return new GPXInputStream();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
