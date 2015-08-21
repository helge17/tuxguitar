package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.plugin.TGOutputStreamPlugin;

public abstract class GTPOutputStreamPlugin extends TGOutputStreamPlugin{
	
	public GTPOutputStreamPlugin(){
		super();
	}
	
	public String getModuleId(){
		return GTPPlugin.MODULE_ID;
	}
}
