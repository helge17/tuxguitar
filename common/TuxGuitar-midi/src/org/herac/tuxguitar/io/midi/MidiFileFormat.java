package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.io.base.TGFileFormat;

public class MidiFileFormat {
	
	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("Midi", "audio/midi", new String[]{"mid","midi"});
	
	public MidiFileFormat() {
		super();
	}
	
	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}
}
