package app.tuxguitar.io.lilypond;

import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.plugin.TGSongWriterPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class LilypondSongWriterPlugin extends TGSongWriterPlugin {

	public static final String MODULE_ID = "tuxguitar-lilypond";

	public LilypondSongWriterPlugin() {
		super();
	}

	public String getModuleId(){
		return MODULE_ID;
	}

	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new LilypondSongWriter();
	}
}
