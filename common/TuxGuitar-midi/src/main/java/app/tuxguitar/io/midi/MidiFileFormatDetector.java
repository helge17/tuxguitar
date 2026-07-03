package app.tuxguitar.io.midi;

import java.io.DataInputStream;
import java.io.InputStream;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatDetector;

public class MidiFileFormatDetector implements TGFileFormatDetector {

	public MidiFileFormatDetector() {
		super();
	}

	public TGFileFormat getFileFormat(InputStream is) {
		try {
			DataInputStream dataInputStream = new DataInputStream(is);
			if (dataInputStream.readInt() == MidiFileHeader.HEADER_MAGIC) {
				return MidiFileFormat.FILE_FORMAT;
			}
			return null;
		} catch (Throwable throwable) {
			return null;
		}
	}
}
