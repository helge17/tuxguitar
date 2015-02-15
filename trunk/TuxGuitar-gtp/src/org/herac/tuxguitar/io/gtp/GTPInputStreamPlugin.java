package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.io.plugin.TGInputStreamPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class GTPInputStreamPlugin extends TGInputStreamPlugin{
	
	private TGInputStreamBase inputStreamBase;
	
	public GTPInputStreamPlugin(){
		super();
	}
	
	protected abstract TGInputStreamBase createInputStream() throws TGPluginException;
	
	protected TGInputStreamBase getInputStream() throws TGPluginException {
		if( this.inputStreamBase == null ) {
			this.inputStreamBase = this.createInputStream();
		}
		return this.inputStreamBase;
	}
	
	public String getModuleId(){
		return GTPPlugin.MODULE_ID;
	}
}
