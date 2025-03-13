package app.tuxguitar.io.gtp;

import app.tuxguitar.io.plugin.TGSongWriterPlugin;

public abstract class GTPOutputStreamPlugin extends TGSongWriterPlugin{

	public GTPOutputStreamPlugin(){
		super();
	}

	public String getModuleId(){
		return GTPPlugin.MODULE_ID;
	}
}
