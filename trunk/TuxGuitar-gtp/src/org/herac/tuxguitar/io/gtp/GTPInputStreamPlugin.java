package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.app.system.plugins.base.TGInputStreamPlugin;
import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GTPInputStreamPlugin extends TGInputStreamPlugin{
	
	private TGInputStreamBase inputStreamBase;
	
	public GTPInputStreamPlugin(TGInputStreamBase inputStreamBase){
		this.inputStreamBase = inputStreamBase;
	}
	
	protected TGInputStreamBase getInputStream() throws TGPluginException {
		return this.inputStreamBase;
	}
	
	public String getModuleId(){
		return GTPPlugin.MODULE_ID;
	}
}
