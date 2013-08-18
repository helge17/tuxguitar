package org.herac.tuxguitar.io.ptb;

import org.herac.tuxguitar.app.system.plugins.base.TGInputStreamPlugin;
import org.herac.tuxguitar.io.base.TGInputStreamBase;

public class PTInputStreamPlugin extends TGInputStreamPlugin{
	
	public static final String MODULE_ID = "tuxguitar-ptb";
	
	protected TGInputStreamBase getInputStream() {
		return new PTInputStream();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
