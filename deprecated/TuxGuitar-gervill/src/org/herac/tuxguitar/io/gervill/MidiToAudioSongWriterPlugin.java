package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.plugin.TGSongWriterPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

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
