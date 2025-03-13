package app.tuxguitar.io.tg;

import app.tuxguitar.io.plugin.TGSongWriterPlugin;

public abstract class TGAbstractSongWriterPlugin extends TGSongWriterPlugin {

	public TGAbstractSongWriterPlugin(){
		super();
	}

	public String getModuleId(){
		return TGCompatPlugin.MODULE_ID;
	}
}
