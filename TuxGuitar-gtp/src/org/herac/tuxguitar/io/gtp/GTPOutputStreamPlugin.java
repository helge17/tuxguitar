package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.io.plugin.TGOutputStreamPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GTPOutputStreamPlugin extends TGOutputStreamPlugin{
	
	private TGOutputStreamBase outputStreamBase;
	
	public GTPOutputStreamPlugin(TGOutputStreamBase outputStreamBase){
		this.outputStreamBase = outputStreamBase;
	}
	
	protected TGOutputStreamBase getOutputStream() throws TGPluginException {
		return this.outputStreamBase;
	}
	
	public String getModuleId(){
		return GTPPlugin.MODULE_ID;
	}
}
