package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.io.plugin.TGOutputStreamPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class GTPOutputStreamPlugin extends TGOutputStreamPlugin{
	
	private TGOutputStreamBase outputStreamBase;
	
	public GTPOutputStreamPlugin(){
		super();
	}
	
	protected abstract TGOutputStreamBase createOutputStream() throws TGPluginException;
	
	protected TGOutputStreamBase getOutputStream() throws TGPluginException {
		if( this.outputStreamBase == null ) {
			this.outputStreamBase = this.createOutputStream();
		}
		return this.outputStreamBase;
	}
	
	public String getModuleId(){
		return GTPPlugin.MODULE_ID;
	}
}
