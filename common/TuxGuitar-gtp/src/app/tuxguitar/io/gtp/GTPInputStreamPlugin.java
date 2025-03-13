package app.tuxguitar.io.gtp;

import app.tuxguitar.io.plugin.TGSongReaderPlugin;

public abstract class GTPInputStreamPlugin extends TGSongReaderPlugin {

	public GTPInputStreamPlugin(){
		super(true);
	}

	public String getModuleId(){
		return GTPPlugin.MODULE_ID;
	}
}
