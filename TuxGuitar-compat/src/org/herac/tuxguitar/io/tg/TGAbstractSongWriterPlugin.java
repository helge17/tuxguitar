package org.herac.tuxguitar.io.tg;

import org.herac.tuxguitar.io.plugin.TGSongWriterPlugin;

public abstract class TGAbstractSongWriterPlugin extends TGSongWriterPlugin {
	
	public TGAbstractSongWriterPlugin(){
		super(false);
	}
	
	public String getModuleId(){
		return TGCompatPlugin.MODULE_ID;
	}
}
