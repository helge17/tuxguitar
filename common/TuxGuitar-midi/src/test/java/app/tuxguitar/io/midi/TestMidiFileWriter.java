package app.tuxguitar.io.midi;

import static app.tuxguitar.io.midi.MidiBytes.PPQ_480;
import static app.tuxguitar.io.midi.MidiBytes.file;
import static app.tuxguitar.io.midi.MidiBytes.track;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.io.midi.base.MidiEvent;
import app.tuxguitar.io.midi.base.MidiMessage;
import app.tuxguitar.io.midi.base.MidiSequence;
import app.tuxguitar.io.midi.base.MidiTrack;

/**
 * Song -> bytes: a sequence is written as a type 1 file, every event preceded by its tick delta as a
 * variable-length quantity (7 bits per byte, high bit set on all but the last byte, at most 4 bytes).
 */
public class TestMidiFileWriter {

	private static final int END_OF_TRACK = 0x2f;
	private static final int TEXT = 0x01;

	@Test
	public void writesShortAndMetaMessages() throws Exception {
		MidiTrack track = new MidiTrack();
		track.add(new MidiEvent(MidiMessage.shortMessage(MidiMessage.NOTE_ON, 2, 0x3c, 0x64), 0));
		track.add(new MidiEvent(MidiMessage.metaMessage(END_OF_TRACK, new byte[0]), 0));

		assertArrayEquals(file(track(0, 0x92, 0x3c, 0x64, 0, 0xff, END_OF_TRACK, 0)), write(track));
	}

	@TestFactory
	public Stream<DynamicTest> writesTickDeltaAsVariableLengthQuantity() {
		return Stream.of(
				delta(0, 0x00),
				delta(0x7f, 0x7f),
				delta(0x80, 0x81, 0x00),
				delta(0x3fff, 0xff, 0x7f),
				delta(0x4000, 0x81, 0x80, 0x00),
				delta(0x4001, 0x81, 0x80, 0x01),
				delta(0x1fffff, 0xff, 0xff, 0x7f),
				delta(0x200000, 0x81, 0x80, 0x80, 0x00),
				delta(0x200001, 0x81, 0x80, 0x80, 0x01),
				delta(0xfffffff, 0xff, 0xff, 0xff, 0x7f),
				// only the low 28 bits are written: a larger delta loses its high bits
				delta(0x10000005, 0x05));
	}

	@Test
	public void writesMetaDataLengthAsVariableLengthQuantity() throws Exception {
		// delta, meta status, type, 2-byte length, 200 data bytes, then delta, meta status, type, length
		int[] body = new int[5 + 200 + 4];
		body[1] = 0xff;
		body[2] = TEXT;
		body[3] = 0x81;
		body[4] = 0x48;
		body[206] = 0xff;
		body[207] = END_OF_TRACK;
		MidiTrack track = new MidiTrack();
		track.add(new MidiEvent(MidiMessage.metaMessage(TEXT, new byte[200]), 0));
		track.add(new MidiEvent(MidiMessage.metaMessage(END_OF_TRACK, new byte[0]), 0));

		assertArrayEquals(file(track(body)), write(track));
	}

	private static DynamicTest delta(long ticks, int... quantity) {
		return DynamicTest.dynamicTest(Long.toHexString(ticks), () -> {
			int[] body = new int[4 + quantity.length + 3];
			body[1] = 0xff;
			body[2] = TEXT;
			System.arraycopy(quantity, 0, body, 4, quantity.length);
			body[4 + quantity.length] = 0xff;
			body[5 + quantity.length] = END_OF_TRACK;
			MidiTrack track = new MidiTrack();
			track.add(new MidiEvent(MidiMessage.metaMessage(TEXT, new byte[0]), 0));
			track.add(new MidiEvent(MidiMessage.metaMessage(END_OF_TRACK, new byte[0]), ticks));

			assertArrayEquals(file(track(body)), write(track));
		});
	}

	private static byte[] write(MidiTrack track) throws IOException {
		MidiSequence sequence = new MidiSequence(MidiSequence.PPQ, PPQ_480);
		sequence.addTrack(track);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new MidiFileWriter().write(sequence, 1, out);
		return out.toByteArray();
	}
}
