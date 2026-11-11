package app.tuxguitar.io.tg;

import java.io.ByteArrayOutputStream;

/**
 * Builds the bytes of a pre-1.0 .tg file (formats 0.7, 0.8 and 0.9) for the reader tests,
 * since TuxGuitar no longer has writers for those formats.
 * Encodes the way the readers decode: bytes, big endian shorts, ints and longs, and strings as a length
 * byte followed by UTF-16 chars.
 */
public final class LegacyStreamEncoder {

	private final ByteArrayOutputStream out = new ByteArrayOutputStream();

	/** Starts a file with the version string the format detector expects. */
	public LegacyStreamEncoder(String version) {
		this.string(version);
	}

	public LegacyStreamEncoder bytes(int... values) {
		for (int value : values) {
			this.out.write(value);
		}
		return this;
	}

	public LegacyStreamEncoder shorts(long... values) {
		return this.numbers(2, values);
	}

	public LegacyStreamEncoder ints(long... values) {
		return this.numbers(4, values);
	}

	public LegacyStreamEncoder longs(long... values) {
		return this.numbers(8, values);
	}

	private LegacyStreamEncoder numbers(int size, long... values) {
		for (long value : values) {
			this.number(size, value);
		}
		return this;
	}

	// big endian, on the given number of bytes
	private void number(int size, long value) {
		for (int shift = 8 * (size - 1); shift >= 0; shift -= 8) {
			this.out.write((int) (value >> shift));
		}
	}

	public LegacyStreamEncoder string(String text) {
		this.bytes(text.length());
		for (char c : text.toCharArray()) {
			this.shorts(c);
		}
		return this;
	}

	/** Name, artist, album and author, then the number of measure headers that follow. */
	public LegacyStreamEncoder songInfo(int headerCount) {
		return this.string("name").string("artist").string("album").string("author").shorts(headerCount);
	}

	/** A channel with no solo/mute flags on GM channel 0, effects channel 1, program 25. */
	public LegacyStreamEncoder channel() {
		return this.bytes(0, 0, 1, 25, 127, 64, 0, 0, 0, 0);
	}

	/** No strings, no offset and a black color: what follows the measures of a track. */
	public LegacyStreamEncoder trackTail() {
		return this.bytes(0, 0).shorts(0, 0, 0);
	}

	public byte[] toByteArray() {
		return this.out.toByteArray();
	}
}
