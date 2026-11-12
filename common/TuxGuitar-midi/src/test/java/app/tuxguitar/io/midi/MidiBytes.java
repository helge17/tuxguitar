package app.tuxguitar.io.midi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Byte-level builders for MIDI file fixtures: raw bytes, track chunks and whole files.
 */
final class MidiBytes {

	static final int PPQ_480 = 480;

	private MidiBytes() {
	}

	static byte[] bytes(int... values) {
		byte[] data = new byte[values.length];
		for (int i = 0; i < values.length; i++) {
			data[i] = (byte) values[i];
		}
		return data;
	}

	// a "MTrk" chunk holding the given event bytes
	static byte[] track(int... body) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytes);
		out.writeInt(MidiFileHeader.TRACK_MAGIC);
		out.writeInt(body.length);
		out.write(bytes(body));
		return bytes.toByteArray();
	}

	// a type 1 file at 480 ticks per quarter note holding the given track chunks
	static byte[] file(byte[]... trackChunks) throws IOException {
		return file(MidiFileHeader.HEADER_MAGIC, 6, 1, trackChunks.length, PPQ_480, trackChunks);
	}

	static byte[] file(int magic, int headerLength, int type, int tracks, int division, byte[]... trackChunks) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytes);
		out.writeInt(magic);
		out.writeInt(headerLength);
		out.writeShort(type);
		out.writeShort(tracks);
		out.writeShort(division);
		out.write(new byte[Math.max(0, headerLength - 6)]);
		for (byte[] chunk : trackChunks) {
			out.write(chunk);
		}
		return bytes.toByteArray();
	}
}
