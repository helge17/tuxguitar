package app.tuxguitar.vst3;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * One request to the VST3 host process, written whole before it is sent (see TGVst3Protocol), and how replies are read.
 */
final class TGVst3Request {

	// reads the operation's fields of a reply
	interface Reply<T> {
		T read(DataInputStream in) throws IOException, TGVst3Exception;
	}

	private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

	TGVst3Request(int operation) {
		this.writeInt(operation);
	}

	TGVst3Request writeInt(int value) {
		return this.write(value, 4);
	}

	TGVst3Request writeLong(long value) {
		return this.write(value, 8);
	}

	TGVst3Request writeFloat(float value) {
		return this.writeInt(Float.floatToIntBits(value));
	}

	TGVst3Request writeDouble(double value) {
		return this.writeLong(Double.doubleToLongBits(value));
	}

	TGVst3Request writeString(String value) {
		return this.writeBytes(value.getBytes(StandardCharsets.UTF_8));
	}

	TGVst3Request writeBytes(byte[] value) {
		this.writeInt(value.length);
		this.bytes.write(value, 0, value.length);
		return this;
	}

	// big-endian
	private TGVst3Request write(long value, int size) {
		for (int shift = 8 * (size - 1); shift >= 0; shift -= 8) {
			this.bytes.write((int) (value >>> shift));
		}
		return this;
	}

	void writeTo(OutputStream out) throws IOException {
		this.bytes.writeTo(out);
	}

	static String readString(DataInputStream in) throws IOException {
		return new String(readBytes(in), StandardCharsets.UTF_8);
	}

	static byte[] readBytes(DataInputStream in) throws IOException {
		byte[] value = new byte[in.readInt()];
		in.readFully(value);
		return value;
	}
}
