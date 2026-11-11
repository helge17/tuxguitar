package app.tuxguitar.io.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * Pins how TGFileFormatManager reads and writes a song (TGSongReaderHelper.read, TGSongWriterHelper.write):
 * the format is taken from the handle, else detected (from the content, the mime type or the format code);
 * the reader or writer of that format does the work; any failure is reported as a TGFileFormatException;
 * and the stream of the handle is closed in every case.
 */
public class TestSongReadWrite {

	private static final TGFileFormat FAKE = new TGFileFormat("Fake", "audio/x-fake", new String[] {"fake"});
	private static final TGFileFormat UNHANDLED = new TGFileFormat("Unhandled", "audio/x-unhandled", new String[] {"unhandled"});
	private static final byte[] CONTENT = {1, 2, 3};

	private final TGFileFormatManager manager = TGFileFormatManager.getInstance(new TGContext());
	private final List<String> calls = new ArrayList<String>();

	// what the fake reader and writer do when called
	private RuntimeException failure;

	public TestSongReadWrite() {
		manager.addReader(new FakeReader());
		manager.addWriter(new FakeWriter());
	}

	// ---- handles ----

	private static TGSongStreamContext context(String attribute, String value) {
		TGSongStreamContext context = new TGSongStreamContext();
		context.setAttribute(attribute, value);
		return context;
	}

	private TGSongWriterHandle writing(OutputStream stream, TGFileFormat format, TGSongStreamContext context) {
		TGSongWriterHandle handle = new TGSongWriterHandle();
		handle.setOutputStream(stream);
		handle.setFormat(format);
		handle.setContext(context);
		return handle;
	}

	private TGSongReaderHandle reading(InputStream stream, TGFileFormat format, TGSongStreamContext context) {
		TGSongReaderHandle handle = new TGSongReaderHandle();
		handle.setInputStream(stream);
		handle.setFormat(format);
		handle.setContext(context);
		handle.setFactory(new TGFactory());
		return handle;
	}

	private static TGFileFormatException fails(Executable executable) {
		return assertThrows(TGFileFormatException.class, executable);
	}

	private static void assertUnsupported(TGFileFormatException exception) {
		assertEquals(TGFileFormatException.UNSUPPORTED_FORMAT, exception.getMessage());
	}

	// ---- a real song, through the native format ----

	@Test
	public void aSongWrittenByFormatCodeReadsBackByItsContent() {
		TGSong song = new TGSongManager().newSong();
		song.setName("round trip");
		TrackedOutput written = new TrackedOutput();
		TGSongWriterHandle writer = writing(written, null, context(TGSongPersistenceHelper.ATTRIBUTE_FORMAT_CODE, "tg"));
		writer.setSong(song);

		manager.write(writer);

		assertEquals("TuxGuitar 2.0", writer.getFormat().getName());
		assertTrue(written.closing.closed);
		TrackedInput input = new TrackedInput(written.toByteArray());
		TGSongReaderHandle reader = reading(input, null, new TGSongStreamContext());

		manager.read(reader);

		assertEquals("TuxGuitar 2.0", reader.getFormat().getName());
		assertNotNull(reader.getSong());
		assertEquals("round trip", reader.getSong().getName());
		assertTrue(input.closing.closed);
	}

	// ---- writing ----

	@Test
	public void writesWithTheWriterOfTheFormatFoundByMimeType() {
		TrackedOutput output = new TrackedOutput();
		TGSongWriterHandle handle = writing(output, null, context(TGSongPersistenceHelper.ATTRIBUTE_MIME_TYPE, "audio/x-fake"));

		manager.write(handle);

		assertSame(FAKE, handle.getFormat());
		assertEquals("[write Fake]", calls.toString());
		assertTrue(output.closing.closed);
	}

	@Test
	public void writingWithoutAKnownFormatIsUnsupported() {
		TrackedOutput output = new TrackedOutput();

		assertUnsupported(fails(() -> manager.write(writing(output, null, new TGSongStreamContext()))));
		assertUnsupported(fails(() -> manager.write(writing(output, UNHANDLED, new TGSongStreamContext()))));
		assertEquals("[]", calls.toString());
		assertTrue(output.closing.closed);
	}

	@Test
	public void writerFailuresAreReportedAsFileFormatErrors() {
		TrackedOutput output = new TrackedOutput();
		failure = new TGFileFormatException("bad song");
		assertSame(failure, fails(() -> manager.write(writing(output, FAKE, null))));

		failure = new IllegalStateException("broken");
		assertSame(failure, fails(() -> manager.write(writing(output, FAKE, null))).getCause());
		assertTrue(output.closing.closed);
	}

	@Test
	public void anOutputThatCannotBeClosedIsAFileFormatError() {
		TrackedOutput output = new TrackedOutput();
		output.closing.failure = new IOException("disk full");

		assertSame(output.closing.failure, fails(() -> manager.write(writing(output, FAKE, null))).getCause());
		assertEquals("[write Fake]", calls.toString());
	}

	// ---- reading ----

	@Test
	public void readsTheWholeContentWithTheReaderOfTheGivenFormat() {
		TrackedInput input = new TrackedInput(CONTENT);
		TGSongReaderHandle handle = reading(input, FAKE, null);

		manager.read(handle);

		assertEquals("[read Fake 1,2,3]", calls.toString());
		assertSame(FAKE, handle.getFormat());
		assertTrue(input.closing.closed);
	}

	@Test
	public void readingWithoutAKnownFormatIsUnsupported() {
		TrackedInput input = new TrackedInput(CONTENT);

		assertUnsupported(fails(() -> manager.read(reading(input, null, new TGSongStreamContext()))));
		assertUnsupported(fails(() -> manager.read(reading(new TrackedInput(CONTENT), UNHANDLED, null))));
		assertEquals("[]", calls.toString());
		assertTrue(input.closing.closed);
	}

	@Test
	public void readerFailuresAreReportedAsFileFormatErrors() {
		failure = new TGFileFormatException("bad file");
		assertSame(failure, fails(() -> manager.read(reading(new TrackedInput(CONTENT), FAKE, null))));

		failure = new IllegalStateException("broken");
		assertSame(failure, fails(() -> manager.read(reading(new TrackedInput(CONTENT), FAKE, null))).getCause());
	}

	@Test
	public void anInputThatCannotBeReadIsAFileFormatError() {
		TrackedInput input = new TrackedInput(CONTENT);
		input.readFailure = new IOException("bad sector");

		assertSame(input.readFailure, fails(() -> manager.read(reading(input, FAKE, null))).getCause());
		assertEquals("[]", calls.toString());
		assertTrue(input.closing.closed);
	}

	@Test
	public void whenTheInputCannotBeClosedEitherTheCloseFailureIsReported() {
		TrackedInput input = new TrackedInput(CONTENT);
		input.readFailure = new IOException("bad sector");
		input.closing.failure = new IOException("device gone");

		assertSame(input.closing.failure, fails(() -> manager.read(reading(input, FAKE, null))).getCause());
	}

	// ---- fakes ----

	private class FakeReader implements TGSongReader {
		public TGFileFormat getFileFormat() {
			return FAKE;
		}

		public void read(TGSongReaderHandle handle) throws TGFileFormatException {
			byte[] content = drain(handle.getInputStream());
			StringBuilder bytes = new StringBuilder();
			for (byte value : content) {
				bytes.append(bytes.length() == 0 ? "" : ",").append(value);
			}
			calls.add("read " + handle.getFormat().getName() + " " + bytes);
			failIfAsked();
		}
	}

	private class FakeWriter implements TGSongWriter {
		public TGFileFormat getFileFormat() {
			return FAKE;
		}

		public void write(TGSongWriterHandle handle) throws TGFileFormatException {
			calls.add("write " + handle.getFormat().getName());
			failIfAsked();
		}
	}

	private void failIfAsked() {
		if (failure != null) {
			throw failure;
		}
	}

	private static byte[] drain(InputStream stream) {
		ByteArrayOutputStream content = new ByteArrayOutputStream();
		try {
			for (int value = stream.read(); value != -1; value = stream.read()) {
				content.write(value);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return content.toByteArray();
	}

	private static class TrackedInput extends InputStream {
		private final InputStream content;
		final Closing closing = new Closing();
		IOException readFailure;

		TrackedInput(byte[] content) {
			this.content = new ByteArrayInputStream(content);
		}

		public int read() throws IOException {
			throwIfSet(readFailure);
			return content.read();
		}

		public void close() throws IOException {
			closing.close();
		}
	}

	private static class TrackedOutput extends ByteArrayOutputStream {
		final Closing closing = new Closing();

		public void close() throws IOException {
			closing.close();
		}
	}

	// whether a stream was closed, and how closing it fails
	private static class Closing {
		boolean closed;
		IOException failure;

		void close() throws IOException {
			closed = true;
			throwIfSet(failure);
		}
	}

	private static void throwIfSet(IOException failure) throws IOException {
		if (failure != null) {
			throw failure;
		}
	}
}
