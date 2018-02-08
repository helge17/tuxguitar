package org.herac.tuxguitar.io.synth;

import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.plugin.TGSongWriterPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGSynthSongWriterPlugin extends TGSongWriterPlugin {
	
	public static final String MODULE_ID = "tuxguitar-synth-export";
	
	public TGSynthSongWriterPlugin() {
		super(false);
	}
	
	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new TGSynthSongWriter(context);
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
