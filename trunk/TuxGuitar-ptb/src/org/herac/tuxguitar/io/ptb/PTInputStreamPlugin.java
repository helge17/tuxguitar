package org.herac.tuxguitar.io.ptb;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.io.plugin.TGInputStreamPlugin;
import org.herac.tuxguitar.util.TGContext;

public class PTInputStreamPlugin extends TGInputStreamPlugin{
	
	public static final String MODULE_ID = "tuxguitar-ptb";
	
	protected TGInputStreamBase createInputStream(TGContext context) {
		return new PTInputStream();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
