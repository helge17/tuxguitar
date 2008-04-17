package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.gui.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGSongExporter;

public class MidiPluginExporter extends TGExporterPlugin{
	
	protected TGSongExporter getExporter() {
		return new MidiSongExporter();
	}
}
