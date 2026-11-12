package app.tuxguitar.io.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/**
 * Pins TGFileFormatUtils.isSupportedFormat: a file name is supported when the text after its last "." is a format
 * code of any of the formats, ignoring case. A missing name, a name without an extension, or an empty extension
 * is never supported.
 */
public class TestFileFormatUtils {

	private static final List<TGFileFormat> FORMATS = List.of(
		new TGFileFormat("TuxGuitar", "audio/x-tuxguitar", new String[] {"tg"}),
		new TGFileFormat("Guitar Pro 5", "audio/x-gtp", new String[] {"gp4", "gp5"}));

	private static DynamicTest supported(String fileName, boolean expected) {
		return DynamicTest.dynamicTest(String.valueOf(fileName), () -> assertEquals(expected, TGFileFormatUtils.isSupportedFormat(FORMATS, fileName)));
	}

	@TestFactory
	public Stream<DynamicTest> aFileIsSupportedByTheCodeAfterItsLastDot() {
		return Stream.of(
			supported("song.tg", true),
			supported("my.song.gp5", true),
			supported("SONG.GP4", true),
			supported("song.mid", false),
			supported("tg.song", false),
			supported("tg", false),
			supported("song.", false),
			supported(null, false));
	}

	@Test
	public void nothingIsSupportedWithoutFormats() {
		assertFalse(TGFileFormatUtils.isSupportedFormat(List.of(), "song.tg"));
	}
}
