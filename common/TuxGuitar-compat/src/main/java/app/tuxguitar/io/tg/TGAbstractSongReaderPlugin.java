package app.tuxguitar.io.tg;

import app.tuxguitar.io.plugin.TGSongReaderPlugin;

public abstract class TGAbstractSongReaderPlugin extends TGSongReaderPlugin {

	public TGAbstractSongReaderPlugin() {
		super(false);
	}

	public String getModuleId(){
		return TGCompatPlugin.MODULE_ID;
	}
}
