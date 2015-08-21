package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiPluginExporter extends TGExporterPlugin{
	
	protected TGRawExporter createExporter(TGContext context) {
		return new MidiSongExporterProvider();
	}
	
	public String getModuleId(){
		return MidiPlugin.MODULE_ID;
	}
}
