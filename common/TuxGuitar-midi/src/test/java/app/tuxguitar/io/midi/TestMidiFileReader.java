package app.tuxguitar.io.midi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.io.midi.base.MidiSequence;

public class TestMidiFileReader {

	private static final int HEADER_MAGIC = MidiFileHeader.HEADER_MAGIC;
	private static final int PPQ_480 = 480;
	// track chunk holding a single "end of track" meta event
	private static final byte[] TRACK = {0x4d, 0x54, 0x72, 0x6b, 0, 0, 0, 4, 0, (byte) 0xff, 0x2f, 0};

	@Test
	public void readsTypeOneFileWithSeveralTracks() throws Exception {
		MidiSequence sequence = read(file(HEADER_MAGIC, 6, 1, 2, PPQ_480, 2));

		assertEquals(2, sequence.countTracks());
		assertEquals(1, sequence.getTrack(1).size());
		assertEquals(MidiSequence.PPQ, sequence.getDivisionType());
		assertEquals(PPQ_480, sequence.getResolution());
	}

	@Test
	public void readsTypeZeroFileWithOneTrack() throws Exception {
		MidiSequence sequence = read(file(HEADER_MAGIC, 6, 0, 1, 0x7fff, 1));

		assertEquals(1, sequence.countTracks());
		assertEquals(1, sequence.getTrack(0).size());
		assertEquals(0x7fff, sequence.getResolution());
	}

	@Test
	public void skipsHeaderBytesBeyondTheStandardLength() throws Exception {
		MidiSequence sequence = read(file(HEADER_MAGIC, 9, 1, 1, PPQ_480, 1));

		assertEquals(1, sequence.getTrack(0).size());
		assertEquals(0x2f, sequence.getTrack(0).get(0).getMessage().getCommand());
	}

	@Test
	public void closesTheStream() throws Exception {
		ClosingStream stream = new ClosingStream(file(HEADER_MAGIC, 6, 1, 1, PPQ_480, 1));

		new MidiFileReader().getSequence(stream);

		assertTrue(stream.closed);
	}

	// each header is rejected with its message; the SMPTE division is 25 frames per second (-25 in the high byte), 40 ticks per frame
	@TestFactory
	public Stream<DynamicTest> rejectsInvalidHeaders() {
		return Stream.of(
				rejected("not a MIDI file: wrong header magic", HEADER_MAGIC + 1, 6, 1, 1, PPQ_480),
				rejected("corrupt MIDI file: wrong header length", HEADER_MAGIC, 5, 1, 1, PPQ_480),
				rejected("corrupt MIDI file: illegal type", HEADER_MAGIC, 6, -1, 1, PPQ_480),
				rejected("corrupt MIDI file: illegal type", HEADER_MAGIC, 6, 3, 1, PPQ_480),
				rejected("this implementation doesn't support type 2 MIDI files", HEADER_MAGIC, 6, 2, 1, PPQ_480),
				rejected("corrupt MIDI file: number of tracks must be positive", HEADER_MAGIC, 6, 1, 0, PPQ_480),
				rejected("corrupt MIDI file: type 0 files must contain exactly one track", HEADER_MAGIC, 6, 0, 2, PPQ_480),
				rejected("corrupt MIDI file: illegal frame division type", HEADER_MAGIC, 6, 1, 1, 0xe728));
	}

	private static DynamicTest rejected(String message, int magic, int headerLength, int type, int tracks, int division) {
		String name = message + " (length " + headerLength + ", type " + type + ", tracks " + tracks + ")";
		return DynamicTest.dynamicTest(name, () -> assertRejected(message, magic, headerLength, type, tracks, division));
	}

	private static void assertRejected(String message, int magic, int headerLength, int type, int tracks, int division) {
		MidiFileException error = assertThrows(MidiFileException.class,
				() -> read(file(magic, headerLength, type, tracks, division, 1)));
		assertEquals(message, error.getMessage());
	}

	private static MidiSequence read(byte[] file) throws Exception {
		return new MidiFileReader().getSequence(new ByteArrayInputStream(file));
	}

	private static byte[] file(int magic, int headerLength, int type, int tracks, int division, int trackChunks) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytes);
		out.writeInt(magic);
		out.writeInt(headerLength);
		out.writeShort(type);
		out.writeShort(tracks);
		out.writeShort(division);
		out.write(new byte[Math.max(0, headerLength - 6)]);
		for (int i = 0; i < trackChunks; i++) {
			out.write(TRACK);
		}
		return bytes.toByteArray();
	}

	private static class ClosingStream extends ByteArrayInputStream {
		private boolean closed;

		private ClosingStream(byte[] bytes) {
			super(bytes);
		}

		@Override
		public void close() throws IOException {
			this.closed = true;
			super.close();
		}
	}
}
