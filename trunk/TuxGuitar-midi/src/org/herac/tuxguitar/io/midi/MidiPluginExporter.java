package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.app.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class MidiPluginExporter extends TGExporterPlugin{
	
	protected TGRawExporter getExporter() {
		return new MidiSongExporter();
	}
	
	public String getModuleId(){
		return MidiPluginList.MODULE_ID;
	}
}
