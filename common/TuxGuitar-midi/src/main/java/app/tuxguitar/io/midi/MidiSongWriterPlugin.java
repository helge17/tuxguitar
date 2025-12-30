package app.tuxguitar.io.midi;

import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.plugin.TGSongWriterPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class MidiSongWriterPlugin extends TGSongWriterPlugin {

	public MidiSongWriterPlugin() {
		super();
	}

	public String getModuleId(){
		return MidiPlugin.MODULE_ID;
	}

	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new MidiSongWriter();
	}
}
