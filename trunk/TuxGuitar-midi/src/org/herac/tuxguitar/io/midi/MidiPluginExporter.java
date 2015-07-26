package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;

public class MidiPluginExporter extends TGExporterPlugin{
	
	protected TGRawExporter getExporter() {
		return new MidiSongExporterProvider();
	}
	
	public String getModuleId(){
		return MidiPlugin.MODULE_ID;
	}
}
