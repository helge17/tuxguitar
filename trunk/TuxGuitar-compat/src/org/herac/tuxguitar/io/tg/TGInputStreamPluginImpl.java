package org.herac.tuxguitar.io.tg;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.io.plugin.TGInputStreamPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGInputStreamPluginImpl extends TGInputStreamPlugin{
	
	private TGInputStreamBase inputStreamBase;
	
	public TGInputStreamPluginImpl(TGInputStreamBase inputStreamBase){
		this.inputStreamBase = inputStreamBase;
	}
	
	protected TGInputStreamBase createInputStream(TGContext context) throws TGPluginException {
		return this.inputStreamBase;
	}
	
	public String getModuleId(){
		return TGCompatPlugin.MODULE_ID;
	}
}
