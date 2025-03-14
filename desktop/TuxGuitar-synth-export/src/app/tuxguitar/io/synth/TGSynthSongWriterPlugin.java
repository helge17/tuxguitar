package app.tuxguitar.io.synth;

import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.plugin.TGSongWriterPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class TGSynthSongWriterPlugin extends TGSongWriterPlugin {

	public static final String MODULE_ID = "tuxguitar-synth-export";

	public TGSynthSongWriterPlugin() {
		super();
	}

	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new TGSynthSongWriter(context);
	}

	public String getModuleId(){
		return MODULE_ID;
	}
}
