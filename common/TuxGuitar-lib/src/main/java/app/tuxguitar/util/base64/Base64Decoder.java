package app.tuxguitar.util.base64;

import java.io.ByteArrayOutputStream;

public class Base64Decoder {

	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	// the value of the '=' padding character: past the 6 bit values of the alphabet
	private static final int PAD = 64;

	private static int get1(byte buf[], int off) {
		return ((buf[off] & 0x3f) << 2) | ((buf[off + 1] & 0x30) >>> 4);
	}

	private static int get2(byte buf[], int off) {
		return ((buf[off + 1] & 0x0f) << 4) | ((buf[off + 2] & 0x3c) >>> 2);
	}

	private static int get3(byte buf[], int off) {
		return ((buf[off + 2] & 0x03) << 6) | (buf[off + 3] & 0x3f);
	}

	// the 6 bit value of a character, PAD for '=', -1 for a character outside the alphabet
	private static int check(int ch) {
		return (ALPHABET + "=").indexOf(ch);
	}

	// characters outside the alphabet are skipped; decoding stops at the first padded group;
	// input whose characters do not fill whole groups is returned unchanged
	public static byte[] decode(byte[] bytes){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte chunk[] = new byte[4];
		int ready = 0;
		for (byte b : bytes) {
			int ch = check(b);
			if (ch >= 0) {
				chunk[ready++] = (byte) ch;
			}
			if (ready == 4) {
				if (decodeGroup(chunk, out) < 3) {
					return out.toByteArray();
				}
				ready = 0;
			}
		}
		return (ready == 0 ? out.toByteArray() : bytes);
	}

	// writes the 1 to 3 bytes of a group of 4 values, returns how many
	private static int decodeGroup(byte chunk[], ByteArrayOutputStream out) {
		int length = (chunk[2] == PAD ? 1 : (chunk[3] == PAD ? 2 : 3));
		byte[] decoded = { (byte) get1(chunk, 0), (byte) get2(chunk, 0), (byte) get3(chunk, 0) };
		out.write(decoded, 0, length);
		return length;
	}
}
