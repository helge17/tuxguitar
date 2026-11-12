package app.tuxguitar.io.midi;

import static app.tuxguitar.io.midi.MidiBytes.bytes;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;

/**
 * A stream is detected as MIDI when it starts with the "MThd" header magic; anything else, including a stream too
 * short to hold the magic, is not recognized.
 */
public class TestMidiFileFormatDetector {

	@Test
	public void detectsTheMidiHeaderMagic() {
		assertSame(MidiFileFormat.FILE_FORMAT, detect(bytes('M', 'T', 'h', 'd', 0, 0, 0, 6)));
	}

	@Test
	public void rejectsOtherMagic() {
		assertNull(detect(bytes('M', 'T', 'r', 'k', 0, 0, 0, 6)));
	}

	@Test
	public void rejectsStreamShorterThanTheMagic() {
		assertNull(detect(bytes('M', 'T', 'h')));
	}

	private static Object detect(byte[] data) {
		return new MidiFileFormatDetector().getFileFormat(new ByteArrayInputStream(data));
	}
}
