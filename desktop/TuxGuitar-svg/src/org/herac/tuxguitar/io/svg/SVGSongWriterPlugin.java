package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.plugin.TGSongWriterPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class SVGSongWriterPlugin extends TGSongWriterPlugin {
	
	public static final String MODULE_ID = "tuxguitar-svg";
	
	public SVGSongWriterPlugin() {
		super(false);
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
	
	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new SVGSongWriter();
	}
}
