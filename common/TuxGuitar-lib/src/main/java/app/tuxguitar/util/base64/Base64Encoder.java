package app.tuxguitar.util.base64;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class Base64Encoder {

	// 19 groups of 3 bytes: 76 characters per line
	private static final int BYTES_PER_LINE = 57;

	private static byte ENCODING[] = {
		(byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H',
		(byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P',
		(byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X',
		(byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
		(byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
		(byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v',
		(byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3',
		(byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/',
		(byte) '='
	};

	private static int get1(byte buf[], int off) {
		return (buf[off] & 0xfc) >> 2;
	}

	private static int get2(byte buf[], int off) {
		return ((buf[off] & 0x3) << 4) | ((buf[off + 1] & 0xf0) >>> 4);
	}

	private static int get3(byte buf[], int off) {
		return ((buf[off + 1] & 0x0f) << 2) | ((buf[off + 2] & 0xc0) >>> 6);
	}

	private static int get4(byte buf[], int off) {
		return buf[off + 2] & 0x3f;
	}

	public static byte[] encode(byte[] bytes) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int tail = (bytes.length % 3);
		int full = (bytes.length - tail);
		for (int off = 0; off < full; off += 3) {
			if (off != 0 && (off % BYTES_PER_LINE) == 0) {
				out.write('\n');
			}
			out.write(encodeGroup(bytes, off), 0, 4);
		}
		if (tail > 0) {
			out.write(encodeTail(bytes, full, tail), 0, 4);
		}
		return out.toByteArray();
	}

	private static byte[] encodeGroup(byte buf[], int off) {
		return new byte[] { ENCODING[get1(buf, off)], ENCODING[get2(buf, off)], ENCODING[get3(buf, off)], ENCODING[get4(buf, off)] };
	}

	// the last 1 or 2 bytes, zero filled to a group, whose characters past the data are '=' padding
	private static byte[] encodeTail(byte bytes[], int off, int tail) {
		byte[] chars = encodeGroup(Arrays.copyOfRange(bytes, off, off + 3), 0);
		Arrays.fill(chars, tail + 1, 4, (byte) '=');
		return chars;
	}
}
