package app.tuxguitar.osc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OSCMessage {

	private final String address;
	private final List<Object> arguments;

	public OSCMessage(String address) {
		this.address = address;
		this.arguments = new ArrayList<Object>();
	}

	public String getAddress() {
		return this.address;
	}

	public void addString(String value) {
		this.arguments.add(value);
	}

	public void addInt(int value) {
		this.arguments.add(Integer.valueOf(value));
	}

	public String getStringArg(int index) {
		return (String) this.arguments.get(index);
	}

	public int getIntArg(int index) {
		return ((Integer) this.arguments.get(index)).intValue();
	}

	public int argCount() {
		return this.arguments.size();
	}

	public byte[] encode() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);

			writeString(dos, this.address);

			StringBuilder typeTags = new StringBuilder(",");
			for (Object arg : this.arguments) {
				if (arg instanceof String) {
					typeTags.append('s');
				} else if (arg instanceof Integer) {
					typeTags.append('i');
				}
			}
			writeString(dos, typeTags.toString());

			for (Object arg : this.arguments) {
				if (arg instanceof String) {
					writeString(dos, (String) arg);
				} else if (arg instanceof Integer) {
					dos.writeInt(((Integer) arg).intValue());
				}
			}

			return baos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeString(DataOutputStream dos, String s) throws IOException {
		byte[] bytes = s.getBytes(StandardCharsets.US_ASCII);
		dos.write(bytes);
		// null terminator + zero padding to next 4-byte boundary
		int totalLen = bytes.length + 1;
		int paddedLen = (totalLen + 3) & ~3;
		for (int i = 0; i < paddedLen - bytes.length; i++) {
			dos.write(0);
		}
	}

	public static OSCMessage decode(byte[] data, int length) {
		ByteBuffer buf = ByteBuffer.wrap(data, 0, length);
		buf.order(ByteOrder.BIG_ENDIAN);

		String address = readString(buf);
		String typeTags = readString(buf);

		OSCMessage msg = new OSCMessage(address);

		// typeTags starts with "," — iterate from index 1
		for (int i = 1; i < typeTags.length(); i++) {
			char tag = typeTags.charAt(i);
			if (tag == 's') {
				msg.arguments.add(readString(buf));
			} else if (tag == 'i') {
				if (buf.remaining() >= 4) {
					msg.arguments.add(Integer.valueOf(buf.getInt()));
				}
			}
			// unknown type tags are silently skipped
		}

		return msg;
	}

	private static String readString(ByteBuffer buf) {
		int start = buf.position();
		int n = 0;
		while (buf.hasRemaining()) {
			byte b = buf.get();
			if (b == 0) {
				break;
			}
			n++;
		}
		String s = new String(buf.array(), start, n, StandardCharsets.US_ASCII);
		// advance to the next 4-byte boundary from the string start
		int totalLen = n + 1;
		int paddedLen = (totalLen + 3) & ~3;
		buf.position(Math.min(start + paddedLen, buf.limit()));
		return s;
	}
}
