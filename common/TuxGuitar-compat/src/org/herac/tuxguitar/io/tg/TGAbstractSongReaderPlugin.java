package org.herac.tuxguitar.io.tg;

import org.herac.tuxguitar.io.plugin.TGSongReaderPlugin;

public abstract class TGAbstractSongReaderPlugin extends TGSongReaderPlugin {
	
	public TGAbstractSongReaderPlugin() {
		super(true);
	}
	
	public String getModuleId(){
		return TGCompatPlugin.MODULE_ID;
	}
}
