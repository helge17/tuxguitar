package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.plugin.TGSongWriterPlugin;

public abstract class GTPOutputStreamPlugin extends TGSongWriterPlugin{

	public GTPOutputStreamPlugin(){
		super();
	}

	public String getModuleId(){
		return GTPPlugin.MODULE_ID;
	}
}
