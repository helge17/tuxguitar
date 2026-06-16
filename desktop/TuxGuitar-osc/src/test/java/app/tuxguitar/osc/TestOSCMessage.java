package app.tuxguitar.osc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestOSCMessage {

	private OSCMessage roundTrip(OSCMessage msg) {
		byte[] bytes = msg.encode();
		return OSCMessage.decode(bytes, bytes.length);
	}

	@Test
	public void testEncodeDecodeAddress() {
		OSCMessage msg = new OSCMessage("/test/address");
		OSCMessage decoded = roundTrip(msg);
		assertEquals("/test/address", decoded.getAddress());
	}

	@Test
	public void testEncodeDecodeString() {
		OSCMessage msg = new OSCMessage("/test");
		msg.addString("hello");
		OSCMessage decoded = roundTrip(msg);
		assertEquals(1, decoded.argCount());
		assertEquals("hello", decoded.getStringArg(0));
	}

	@Test
	public void testEncodeDecodeInt() {
		OSCMessage msg = new OSCMessage("/test");
		msg.addInt(42);
		OSCMessage decoded = roundTrip(msg);
		assertEquals(1, decoded.argCount());
		assertEquals(42, decoded.getIntArg(0));
	}

	@Test
	public void testEncodeDecodeMixed() {
		OSCMessage msg = new OSCMessage("/test/mixed");
		msg.addString("first");
		msg.addInt(99);
		msg.addString("last");
		OSCMessage decoded = roundTrip(msg);
		assertEquals(3, decoded.argCount());
		assertEquals("first", decoded.getStringArg(0));
		assertEquals(99, decoded.getIntArg(1));
		assertEquals("last", decoded.getStringArg(2));
	}

	@Test
	public void testPaddingAlignment() {
		int[] lengths = { 0, 1, 2, 3, 4, 7, 8 };
		for (int len : lengths) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < len; i++) {
				sb.append('a');
			}
			String value = sb.toString();
			OSCMessage msg = new OSCMessage("/pad");
			msg.addString(value);
			OSCMessage decoded = roundTrip(msg);
			assertEquals(1, decoded.argCount(), "argCount failed for length " + len);
			assertEquals(value, decoded.getStringArg(0), "value mismatch for length " + len);
		}
	}

	@Test
	public void testMultipleArgs() {
		OSCMessage msg = new OSCMessage("/multi");
		msg.addString("alpha");
		msg.addInt(1);
		msg.addString("beta");
		msg.addInt(2);
		msg.addString("gamma");
		OSCMessage decoded = roundTrip(msg);
		assertEquals(5, decoded.argCount());
		assertEquals("alpha", decoded.getStringArg(0));
		assertEquals(1, decoded.getIntArg(1));
		assertEquals("beta", decoded.getStringArg(2));
		assertEquals(2, decoded.getIntArg(3));
		assertEquals("gamma", decoded.getStringArg(4));
	}

	@Test
	public void testEmptyArgs() {
		OSCMessage msg = new OSCMessage("/empty");
		OSCMessage decoded = roundTrip(msg);
		assertEquals(0, decoded.argCount());
	}
}
