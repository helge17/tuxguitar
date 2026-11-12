package app.tuxguitar.io.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import app.tuxguitar.util.TGContext;

/**
 * Pins how TGFileFormatManager finds the format to read by format code or mime type: among the common read formats
 * first, then among the other formats that are not native; with an explicit choice, among all readers (null), the
 * common ones (true) or the others (false). Nothing is found for a missing code or mime type.
 *
 * The manager reads the native format "tg" (common), "Shared" ("abc", a common format), "Other" ("abc" and "xyz",
 * not common) and "Fake native" (only the native code "tg", not common). Formats are read back by name, "-" for none.
 */
public class TestFileFormatManager {

	private static final TGFileFormat SHARED = new TGFileFormat("Shared", "audio/x-shared", new String[] {"abc"});
	private static final TGFileFormat OTHER = new TGFileFormat("Other", "audio/x-other", new String[] {"abc", "xyz"});
	private static final TGFileFormat FAKE_NATIVE = new TGFileFormat("Fake native", "audio/x-tuxguitar", new String[] {"tg"});
	private static final TGFileFormat WRITTEN_ONLY = new TGFileFormat("Written only", "audio/x-written", new String[] {"wo"});

	private final TGFileFormatManager manager = TGFileFormatManager.getInstance(new TGContext());

	public TestFileFormatManager() {
		manager.addReader(reader(OTHER));
		manager.addReader(reader(SHARED));
		manager.addReader(reader(FAKE_NATIVE));
		manager.addCommonReadFileFormat(SHARED);
		manager.addWriter(new TGSongWriter() {
			public TGFileFormat getFileFormat() { return WRITTEN_ONLY; }
			public void write(TGSongWriterHandle handle) { }
		});
	}

	private static TGSongReader reader(TGFileFormat format) {
		return new TGSongReader() {
			public TGFileFormat getFileFormat() { return format; }
			public void read(TGSongReaderHandle handle) { }
		};
	}

	// the name of the format found for each key, " | " separated
	private static String found(Function<String, TGFileFormat> find, String... keys) {
		StringBuilder names = new StringBuilder();
		for (String key : keys) {
			TGFileFormat format = find.apply(key);
			names.append(names.length() == 0 ? "" : " | ").append(format == null ? "-" : format.getName());
		}
		return names.toString();
	}

	@Test
	public void findsTheReadFormatOfACodeAmongTheCommonFormatsFirst() {
		assertEquals("Shared | Other | TuxGuitar 2.0 | - | -",
			found(manager::findReaderFileFormatByCode, "abc", "xyz", "tg", "mid", null));
	}

	@Test
	public void findsTheReadFormatOfACodeAmongTheChosenReaders() {
		assertEquals("Other | Other | -", found(code -> manager.findReaderFileFormatByCode(code, false), "abc", "xyz", "tg"));
		assertEquals("Shared | - | TuxGuitar 2.0", found(code -> manager.findReaderFileFormatByCode(code, true), "abc", "xyz", "tg"));
		assertEquals("TuxGuitar 2.0 | Other", found(code -> manager.findReaderFileFormatByCode(code, null), "tg", "abc"));
	}

	@Test
	public void findsTheReadFormatOfAMimeTypeAmongTheCommonFormatsFirst() {
		assertEquals("Shared | Other | TuxGuitar 2.0 | - | -", found(manager::findReaderFileFormatByMimeType,
			"audio/x-shared", "audio/x-other", "application/x-tuxguitar", "audio/x-written", null));
	}

	@Test
	public void findsTheReadFormatOfAMimeTypeAmongTheChosenReaders() {
		assertEquals("- | Other", found(mime -> manager.findReaderFileFormatByMimeType(mime, false), "audio/x-shared", "audio/x-other"));
		assertEquals("Shared | -", found(mime -> manager.findReaderFileFormatByMimeType(mime, true), "audio/x-shared", "audio/x-other"));
	}

	// looks wrong, pinned as is: the write format of a mime type is searched among the read formats
	@Test
	public void findsTheWriteFormatOfAMimeTypeAmongTheReadFormats() {
		assertEquals("- | Shared | TuxGuitar 2.0 | -", found(manager::findWriterFileFormatByMimeType,
			"audio/x-written", "audio/x-shared", "application/x-tuxguitar", null));
	}

	@Test
	public void theNativeFormatCodeIsTg() {
		assertEquals(List.of(true, false, false), List.of(
			manager.isNativeFileFormat("tg"), manager.isNativeFileFormat("abc"), manager.isNativeFileFormat((String) null)));
	}
}
