package app.tuxguitar.io.midi;

import app.tuxguitar.io.base.TGFileFormat;

public class MidiFileFormat {

	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("MIDI", "audio/midi", new String[]{"mid","midi"});

	public MidiFileFormat() {
		super();
	}

	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}
}
