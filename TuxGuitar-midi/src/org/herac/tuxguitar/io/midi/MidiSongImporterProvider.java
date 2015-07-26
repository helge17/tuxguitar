package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class MidiSongImporterProvider extends MidiSongProvider implements TGLocalFileImporter{
	
	public MidiSongImporterProvider(){
		super();
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Midi", new String[]{"mid","midi"});
	}
	
	public String getImportName() {
		return "Midi";
	}

	public TGSongStream openStream(TGSongStreamContext context) {
		return new MidiSongImporter(context);
	}
}