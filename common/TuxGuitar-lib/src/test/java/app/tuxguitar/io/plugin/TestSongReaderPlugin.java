package app.tuxguitar.io.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatDetector;
import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

/**
 * Pins what a song reader plugin registers in the file format manager: connect adds its reader (and its format to
 * the common read formats when the plugin says so) and its format detector, if it has one; each is created once,
 * except a missing detector, which is asked for again on the next connect. disconnect removes them all. A failure
 * while connecting is wrapped in a TGPluginException.
 *
 * The manager is read back as "readers=N common=yes|no detectors=N", counts including the manager's defaults.
 */
public class TestSongReaderPlugin {

	private static final TGFileFormat FORMAT = new TGFileFormat("Test", "audio/x-test", new String[] {"tst"});

	private final TGContext context = new TGContext();
	private final TGFileFormatManager manager = TGFileFormatManager.getInstance(this.context);

	private static final TGFileFormatDetector DETECTOR = inputStream -> FORMAT;

	private static final class Plugin extends TGSongReaderPlugin {
		private final TGFileFormatDetector detector;
		private RuntimeException failure;
		private int readers;
		private int detectors;

		Plugin(boolean commonFileFormat, TGFileFormatDetector detector) {
			super(commonFileFormat);
			this.detector = detector;
		}

		public String getModuleId() {
			return "test";
		}

		protected TGSongReader createInputStream(TGContext context) {
			this.readers++;
			return new TGSongReader() {
				public TGFileFormat getFileFormat() {
					return FORMAT;
				}

				public void read(TGSongReaderHandle handle) {
					throw new UnsupportedOperationException();
				}
			};
		}

		protected TGFileFormatDetector createFileFormatDetector(TGContext context) {
			this.detectors++;
			this.failIfAsked();
			return this.detector;
		}

		private void failIfAsked() {
			if (this.failure != null) {
				throw this.failure;
			}
		}
	}

	private String describe() {
		return "readers=" + this.manager.findSongReaders(null).size()
			+ " common=" + (this.manager.isCommonReadFileFormat(FORMAT) ? "yes" : "no")
			+ " detectors=" + this.manager.getFileFormatDetectors().size();
	}

	// the manager after connect, connect again, disconnect, disconnect again; then what the plugin created
	private List<String> connectTwiceThenDisconnectTwice(Plugin plugin) throws TGPluginException {
		List<String> states = new ArrayList<String>();
		plugin.connect(this.context);
		states.add(describe());
		plugin.connect(this.context);
		states.add(describe());
		plugin.disconnect(this.context);
		states.add(describe());
		plugin.disconnect(this.context);
		states.add(describe());
		states.add("created readers=" + plugin.readers + " detectors=" + plugin.detectors);
		return states;
	}

	@Test
	public void connectRegistersTheReaderItsCommonFormatAndTheDetectorOnce() throws TGPluginException {
		assertEquals("readers=1 common=no detectors=1", describe());
		assertEquals(Arrays.asList(
				"readers=2 common=yes detectors=2",
				"readers=2 common=yes detectors=2",
				"readers=1 common=no detectors=1",
				"readers=1 common=no detectors=1",
				"created readers=1 detectors=1"),
			connectTwiceThenDisconnectTwice(new Plugin(true, DETECTOR)));
	}

	@Test
	public void aFormatThatIsNotCommonAndAMissingDetectorAreNotRegistered() throws TGPluginException {
		assertEquals(Arrays.asList(
				"readers=2 common=no detectors=1",
				"readers=2 common=no detectors=1",
				"readers=1 common=no detectors=1",
				"readers=1 common=no detectors=1",
				"created readers=1 detectors=2"),
			connectTwiceThenDisconnectTwice(new Plugin(false, null)));
	}

	@Test
	public void aPluginWithAFormatThatIsNotCommonLeavesTheCommonFormatsAlone() throws TGPluginException {
		this.manager.addCommonReadFileFormat(FORMAT);
		Plugin plugin = new Plugin(false, null);
		plugin.connect(this.context);
		plugin.disconnect(this.context);

		assertEquals("readers=1 common=yes detectors=1", describe());
	}

	@Test
	public void aFailureWhileConnectingIsWrapped() {
		Plugin plugin = new Plugin(true, DETECTOR);
		plugin.failure = new IllegalStateException("broken");

		TGPluginException exception = assertThrows(TGPluginException.class, () -> plugin.connect(this.context));
		assertEquals("broken", exception.getMessage());
		assertSame(plugin.failure, exception.getCause());
	}
}
