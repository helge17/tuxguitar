package org.herac.tuxguitar.io.musicxml;

import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.plugin.TGSongWriterPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MusicXMLSongWriterPlugin extends TGSongWriterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-musicxml";
	
	public MusicXMLSongWriterPlugin() {
		super(false);
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}

	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new MusicXMLSongWriter();
	}
}
