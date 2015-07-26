package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class MidiSongExporterProvider extends MidiSongProvider implements TGLocalFileExporter{
	
	public String getExportName() {
		return "Midi";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Midi", new String[]{"mid","midi"});
	}
	
	public TGSongStream openStream(TGSongStreamContext context) {
		return new MidiSongExporter(context);
	}
}
