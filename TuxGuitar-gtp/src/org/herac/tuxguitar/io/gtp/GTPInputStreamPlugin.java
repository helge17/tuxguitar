package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.plugin.TGInputStreamPlugin;

public abstract class GTPInputStreamPlugin extends TGInputStreamPlugin{
	
	public GTPInputStreamPlugin(){
		super();
	}
	
	public String getModuleId(){
		return GTPPlugin.MODULE_ID;
	}
}
