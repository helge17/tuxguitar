package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.gui.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class MidiPluginExporter extends TGExporterPlugin{
	
	protected TGRawExporter getExporter() {
		return new MidiSongExporter();
	}
}
