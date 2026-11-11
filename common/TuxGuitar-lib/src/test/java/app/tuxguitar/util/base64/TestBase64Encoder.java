package app.tuxguitar.util.base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;

/**
 * Pins Base64Encoder.encode: standard base64 alphabet and '=' padding, broken into lines of 76 characters
 * (19 groups of 3 bytes) by a bare '\n', with no trailing newline. A padded last group is not wrapped:
 * it stays at the end of the full line before it.
 */
public class TestBase64Encoder {

	private static String encode(byte[] bytes) {
		return new String(Base64Encoder.encode(bytes), StandardCharsets.US_ASCII);
	}

	private static String encode(String text) {
		return encode(text.getBytes(StandardCharsets.US_ASCII));
	}

	private static byte[] bytes(int length) {
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			bytes[i] = (byte) (i * 37 + 11);
		}
		return bytes;
	}

	private static String mime(byte[] bytes) {
		return Base64.getMimeEncoder(76, new byte[] {'\n'}).encodeToString(bytes);
	}

	@Test
	public void encodesNothingAsNothing() {
		assertEquals("", encode(""));
	}

	@Test
	public void padsTheLastGroupToFourCharacters() {
		assertEquals("TWFu", encode("Man"));
		assertEquals("TWE=", encode("Ma"));
		assertEquals("TQ==", encode("M"));
		assertEquals("dXNlcjpwYXNzd29yZA==", encode("user:password"));
	}

	@Test
	public void usesTheWholeAlphabetIncludingHighBytes() {
		assertEquals("+/+/", encode(new byte[] {(byte) 0xfb, (byte) 0xff, (byte) 0xbf}));
		assertEquals("AAAA", encode(new byte[] {0, 0, 0}));
	}

	@Test
	public void wrapsFullGroupsIntoLinesOf76Characters() {
		for (int length = 0; length <= 3000; length++) {
			boolean paddedAfterAFullLine = (length > 57 && (length % 57) > 0 && (length % 57) < 3);
			if (!paddedAfterAFullLine) {
				byte[] bytes = bytes(length);
				assertEquals(mime(bytes), encode(bytes), "length " + length);
			}
		}
	}

	@Test
	public void aPaddedLastGroupStaysOnTheFullLineBeforeIt() {
		for (int length : new int[] {58, 59, 115, 116, 1027, 1028}) {
			byte[] bytes = bytes(length);
			String mime = mime(bytes);
			int lastBreak = mime.lastIndexOf('\n');
			String expected = mime.substring(0, lastBreak) + mime.substring(lastBreak + 1);
			assertEquals(expected, encode(bytes), "length " + length);
			assertEquals(80, encode(bytes).length() - encode(bytes).lastIndexOf('\n') - 1, "length " + length);
		}
	}
}
