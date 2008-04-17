package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.gui.system.plugins.base.TGImporterPlugin;
import org.herac.tuxguitar.io.base.TGSongImporter;

public class MidiPluginImporter extends TGImporterPlugin{
	
	protected TGSongImporter getImporter() {
		return new MidiSongImporter();
	}
}
