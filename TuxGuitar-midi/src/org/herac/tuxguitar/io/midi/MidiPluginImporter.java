package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.io.base.TGRawImporter;
import org.herac.tuxguitar.io.plugin.TGImporterPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MidiPluginImporter extends TGImporterPlugin{
	
	protected TGRawImporter createImporter(TGContext context) {
		return new MidiSongImporterProvider();
	}
	
	public String getModuleId(){
		return MidiPlugin.MODULE_ID;
	}
}
