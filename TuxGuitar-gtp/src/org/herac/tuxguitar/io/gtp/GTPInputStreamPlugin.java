package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.plugin.TGSongReaderPlugin;

public abstract class GTPInputStreamPlugin extends TGSongReaderPlugin {
	
	public GTPInputStreamPlugin(){
		super(true);
	}
	
	public String getModuleId(){
		return GTPPlugin.MODULE_ID;
	}
}
