package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.io.base.TGRawImporter;
import org.herac.tuxguitar.io.plugin.TGImporterPlugin;

public class MidiPluginImporter extends TGImporterPlugin{
	
	protected TGRawImporter getImporter() {
		return new MidiSongImporter();
	}
	
	public String getModuleId(){
		return MidiPlugin.MODULE_ID;
	}
}
