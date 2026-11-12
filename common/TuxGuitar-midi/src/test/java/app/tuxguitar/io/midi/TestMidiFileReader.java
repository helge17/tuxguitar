package app.tuxguitar.io.midi;

import static app.tuxguitar.io.midi.MidiBytes.PPQ_480;
import static app.tuxguitar.io.midi.MidiBytes.file;
import static app.tuxguitar.io.midi.MidiBytes.track;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.io.midi.base.MidiEvent;
import app.tuxguitar.io.midi.base.MidiMessage;
import app.tuxguitar.io.midi.base.MidiSequence;
import app.tuxguitar.io.midi.base.MidiTrack;

public class TestMidiFileReader {

	private static final int HEADER_MAGIC = MidiFileHeader.HEADER_MAGIC;
	private static final int END_OF_TRACK = 0x2f;

	@Test
	public void readsTypeOneFileWithSeveralTracks() throws Exception {
		MidiSequence sequence = read(header(HEADER_MAGIC, 6, 1, 2, PPQ_480, 2));

		assertEquals(2, sequence.countTracks());
		assertEquals(1, sequence.getTrack(1).size());
		assertEquals(MidiSequence.PPQ, sequence.getDivisionType());
		assertEquals(PPQ_480, sequence.getResolution());
	}

	@Test
	public void readsTypeZeroFileWithOneTrack() throws Exception {
		MidiSequence sequence = read(header(HEADER_MAGIC, 6, 0, 1, 0x7fff, 1));

		assertEquals(1, sequence.countTracks());
		assertEquals(1, sequence.getTrack(0).size());
		assertEquals(0x7fff, sequence.getResolution());
	}

	@Test
	public void skipsHeaderBytesBeyondTheStandardLength() throws Exception {
		MidiSequence sequence = read(header(HEADER_MAGIC, 9, 1, 1, PPQ_480, 1));

		assertEquals(1, sequence.getTrack(0).size());
		assertEquals(END_OF_TRACK, sequence.getTrack(0).get(0).getMessage().getCommand());
	}

	@Test
	public void closesTheStream() throws Exception {
		ClosingStream stream = new ClosingStream(header(HEADER_MAGIC, 6, 1, 1, PPQ_480, 1));

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

	// every channel voice status keeps its data bytes: program change and channel pressure have one, the others two
	@TestFactory
	public Stream<DynamicTest> readsEveryChannelMessageWithItsDataBytes() {
		return Stream.of(
				channelMessage("note off", 0x81, 0x3c, 0x40),
				channelMessage("note on", 0x92, 0x3c, 0x64),
				channelMessage("key pressure", 0xa3, 0x3c, 0x20),
				channelMessage("control change", 0xb4, 0x07, 0x50),
				channelMessage("program change", 0xc5, 0x19),
				channelMessage("channel pressure", 0xd6, 0x30),
				channelMessage("pitch bend", 0xef, 0x00, 0x40));
	}

	@Test
	public void runningStatusRepeatsTheLastTwoByteStatus() throws Exception {
		assertEquals(Arrays.asList(
				"@0 short 144 [-112, 60, 100]",
				"@16 short 144 [-112, 62, 80]",
				"@16 meta 47 []"),
			events(0, 0x90, 0x3c, 0x64, 0x10, 0x3e, 0x50, 0, 0xff, END_OF_TRACK, 0));
	}

	@Test
	public void runningStatusRepeatsTheLastOneByteStatus() throws Exception {
		assertEquals(Arrays.asList(
				"@0 short 192 [-59, 7]",
				"@3 short 192 [-59, 8]",
				"@3 meta 47 []"),
			events(0, 0xc5, 0x07, 0x03, 0x08, 0, 0xff, END_OF_TRACK, 0));
	}

	@Test
	public void readsMetaEventWithItsTypeAndData() throws Exception {
		assertEquals(Arrays.asList(
				"@5 meta 3 [65, 66]",
				"@5 meta 47 []"),
			events(0x05, 0xff, 0x03, 0x02, 'A', 'B', 0, 0xff, END_OF_TRACK, 0));
	}

	@Test
	public void skipsSystemExclusiveEvents() throws Exception {
		assertEquals(Arrays.asList("@2 meta 47 []"),
			events(0, 0xf0, 0x03, 0x7e, 0x7f, 0xf7, 0, 0xf7, 0x01, 0x01, 0x02, 0xff, END_OF_TRACK, 0));
	}

	// a system common or real time status other than sysex and meta yields no event and reads no data byte
	@Test
	public void ignoresOtherSystemStatusesWithoutReadingData() throws Exception {
		assertEquals(Arrays.asList("@0 meta 47 []"),
			events(0, 0xf1, 0, 0xfe, 0, 0xff, END_OF_TRACK, 0));
	}

	// a data byte without running status: none set yet, or cancelled by a sysex or meta event
	@TestFactory
	public Stream<DynamicTest> rejectsDataByteWithoutRunningStatus() {
		return Stream.of(
				statusMissing("no status yet", 0, 0x3c, 0x64),
				statusMissing("after sysex", 0, 0x90, 0x3c, 0x64, 0, 0xf0, 0x01, 0xf7, 0, 0x3c, 0x00),
				statusMissing("after meta event", 0, 0x90, 0x3c, 0x64, 0, 0xff, 0x01, 0x00, 0, 0x3c, 0x00));
	}

	@Test
	public void runningStatusSurvivesSysexAndMetaWhenCancelIsOff() throws Exception {
		MidiFileReader.CANCEL_RUNNING_STATUS_ON_META_AND_SYSEX = false;
		try {
			assertEquals(Arrays.asList(
					"@0 short 144 [-112, 60, 100]",
					"@0 meta 1 []",
					"@0 short 144 [-112, 60, 0]",
					"@0 short 144 [-112, 61, 0]",
					"@0 meta 47 []"),
				events(0, 0x90, 0x3c, 0x64, 0, 0xff, 0x01, 0x00, 0, 0x3c, 0x00, 0, 0xf0, 0x01, 0xf7, 0, 0x3d, 0x00,
						0, 0xff, END_OF_TRACK, 0));
		} finally {
			MidiFileReader.CANCEL_RUNNING_STATUS_ON_META_AND_SYSEX = true;
		}
	}

	private static DynamicTest channelMessage(String name, int status, int... data) {
		return DynamicTest.dynamicTest(name, () -> {
			int[] body = new int[data.length + 2];
			body[1] = status;
			System.arraycopy(data, 0, body, 2, data.length);
			MidiMessage message = sequence(track(body)).getTrack(0).get(0).getMessage();

			assertEquals(status & 0xf0, message.getCommand());
			assertEquals(data.length + 1, message.getData().length);
			assertEquals((byte) status, message.getData()[0]);
			assertEquals((byte) data[data.length - 1], message.getData()[data.length]);
		});
	}

	private static DynamicTest statusMissing(String name, int... body) {
		return DynamicTest.dynamicTest(name, () -> {
			MidiFileException error = assertThrows(MidiFileException.class, () -> events(body));
			assertEquals("corrupt MIDI file: status byte missing", error.getMessage());
		});
	}

	// the events of a one-track file holding the given track body, as "@tick type command [data]"
	private static List<String> events(int... body) throws Exception {
		MidiTrack track = sequence(track(body)).getTrack(0);
		List<String> events = new ArrayList<String>();
		for (int i = 0; i < track.size(); i++) {
			MidiEvent event = track.get(i);
			MidiMessage message = event.getMessage();
			String type = (message.getType() == MidiMessage.TYPE_SHORT) ? "short" : "meta";
			events.add("@" + event.getTick() + " " + type + " " + message.getCommand() + " " + Arrays.toString(message.getData()));
		}
		return events;
	}

	private static DynamicTest rejected(String message, int magic, int headerLength, int type, int tracks, int division) {
		String name = message + " (length " + headerLength + ", type " + type + ", tracks " + tracks + ")";
		return DynamicTest.dynamicTest(name, () -> assertRejected(message, magic, headerLength, type, tracks, division));
	}

	private static void assertRejected(String message, int magic, int headerLength, int type, int tracks, int division) {
		MidiFileException error = assertThrows(MidiFileException.class,
				() -> read(header(magic, headerLength, type, tracks, division, 1)));
		assertEquals(message, error.getMessage());
	}

	private static MidiSequence sequence(byte[] trackChunk) throws Exception {
		return read(file(trackChunk));
	}

	private static MidiSequence read(byte[] file) throws Exception {
		return new MidiFileReader().getSequence(new ByteArrayInputStream(file));
	}

	// a file with the given header fields and that many chunks holding a single "end of track" meta event
	private static byte[] header(int magic, int headerLength, int type, int tracks, int division, int trackChunks) throws IOException {
		byte[][] chunks = new byte[trackChunks][];
		for (int i = 0; i < trackChunks; i++) {
			chunks[i] = track(0, 0xff, END_OF_TRACK, 0);
		}
		return file(magic, headerLength, type, tracks, division, chunks);
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
