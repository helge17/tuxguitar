package app.tuxguitar.util.base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;

/**
 * Pins Base64Decoder.decode: standard base64 alphabet; any character outside it (line breaks included) is skipped;
 * decoding stops at the first padded group; input whose characters do not fill whole groups of 4 is returned
 * unchanged.
 */
public class TestBase64Decoder {

	private static byte[] ascii(String text) {
		return text.getBytes(StandardCharsets.US_ASCII);
	}

	private static String decode(String text) {
		return new String(Base64Decoder.decode(ascii(text)), StandardCharsets.US_ASCII);
	}

	private static byte[] bytes(int length) {
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			bytes[i] = (byte) (i * 37 + 11);
		}
		return bytes;
	}

	@Test
	public void decodesNothingAsNothing() {
		assertEquals("", decode(""));
	}

	@Test
	public void decodesPaddedGroups() {
		assertEquals("Man", decode("TWFu"));
		assertEquals("Ma", decode("TWE="));
		assertEquals("M", decode("TQ=="));
		assertEquals("user:password", decode("dXNlcjpwYXNzd29yZA=="));
	}

	@Test
	public void usesTheWholeAlphabetIncludingHighBytes() {
		assertArrayEquals(new byte[] {(byte) 0xfb, (byte) 0xff, (byte) 0xbf}, Base64Decoder.decode(ascii("+/+/")));
		assertArrayEquals(new byte[] {0, 0, 0}, Base64Decoder.decode(ascii("AAAA")));
	}

	@Test
	public void decodesWhatTheMimeEncoderWrites() {
		for (int length = 0; length <= 3000; length++) {
			byte[] bytes = bytes(length);
			byte[] encoded = Base64.getMimeEncoder(76, new byte[] {'\r', '\n'}).encode(bytes);
			assertArrayEquals(bytes, Base64Decoder.decode(encoded), "length " + length);
		}
	}

	@Test
	public void skipsCharactersOutsideTheAlphabet() {
		assertEquals("Man", decode(" T\tW*F\nu-"));
		byte[] withHighBytes = {'T', (byte) 0xc3, 'W', (byte) 0x80, 'F', 'u'};
		assertEquals("Man", new String(Base64Decoder.decode(withHighBytes), StandardCharsets.US_ASCII));
	}

	@Test
	public void stopsAtTheFirstPaddedGroup() {
		assertEquals("M", decode("TQ==TWFu"));
		assertEquals("Ma", decode("TWE=TWFu"));
	}

	@Test
	public void returnsAnIncompleteInputUnchanged() {
		byte[] incomplete = ascii("TWFuTW");
		assertSame(incomplete, Base64Decoder.decode(incomplete));
	}

	@Test
	public void decodesOnlyNoiseAsNothing() {
		assertEquals("", decode("*\n*"));
	}
}
