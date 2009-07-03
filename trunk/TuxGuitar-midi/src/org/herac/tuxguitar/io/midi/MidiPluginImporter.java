package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.gui.system.plugins.base.TGImporterPlugin;
import org.herac.tuxguitar.io.base.TGRawImporter;

public class MidiPluginImporter extends TGImporterPlugin{
	
	protected TGRawImporter getImporter() {
		return new MidiSongImporter();
	}
}
