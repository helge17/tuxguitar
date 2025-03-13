package app.tuxguitar.io.gervill;

import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.plugin.TGSongWriterPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class MidiToAudioSongWriterPlugin extends TGSongWriterPlugin {

	public static final String MODULE_ID = "tuxguitar-gervill";

	private boolean available;

	public MidiToAudioSongWriterPlugin() {
		super(false);

		this.available = MidiToAudioSynth.instance().isAvailable();
	}

	public void connect(TGContext context) throws TGPluginException {
		if( this.available ){
			super.connect(context);
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		if( this.available ){
			super.disconnect(context);
		}
	}

	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		if( this.available ){
			return new MidiToAudioSongWriter();
		}
		return null;
	}

	public String getModuleId(){
		return MODULE_ID;
	}
}
