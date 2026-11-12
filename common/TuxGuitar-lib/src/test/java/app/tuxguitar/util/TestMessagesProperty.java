package app.tuxguitar.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;
import java.util.Properties;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.RegisterExtension;

import app.tuxguitar.resource.TGResourceBundle;

/**
 * Pins how TGMessagesManager.getProperty reads a message: the loaded text of its key, or the key itself when the
 * message is missing or no messages are loaded. A missing message is reported (printed), except instrument and
 * percussion names, which may be missing. With arguments, each "{i}" is replaced by the i-th argument as text,
 * leaving every other character (apostrophes too) as is; when an argument cannot be turned into text, the message
 * is returned without replacements.
 *
 * A message without arguments is read back as "text | what was printed".
 */
public class TestMessagesProperty {

	private static final String WELCOME = "Welcome {0}, it's {1} o'clock, {0}!";

	@RegisterExtension
	final SystemErrCapture err = new SystemErrCapture();

	@BeforeEach
	public void loadMessages() {
		Properties properties = new Properties();
		properties.setProperty("greeting", "Hello");
		properties.setProperty("welcome", WELCOME);
		TGMessagesManager.getInstance().setResources(new TGResourceBundle(Locale.ROOT, properties));
	}

	// messages without any text: every message reads as its key, as before any messages were loaded
	@AfterEach
	public void loadNoText() {
		TGMessagesManager.getInstance().setResources(new TGResourceBundle(Locale.ROOT, new Properties()));
	}

	private DynamicTest reads(String key, String expected) {
		return DynamicTest.dynamicTest(key, () -> {
			this.err.reset();
			assertEquals(expected, TGMessagesManager.getProperty(key) + " | " + this.err.printed().trim());
		});
	}

	@TestFactory
	public Stream<DynamicTest> readsTheTextOfAMessageOrItsKey() {
		return Stream.of(
			reads("greeting", "Hello | "),
			reads("farewell", "farewell | Message farewell not found"),
			reads("midi.instrument-500", "midi.instrument-500 | "),
			reads("midi.percussion-key-500", "midi.percussion-key-500 | "));
	}

	@Test
	public void readsEveryMessageAsItsKeyWithoutMessages() {
		TGMessagesManager.getInstance().setResources(null);

		assertEquals("greeting | ", TGMessagesManager.getProperty("greeting") + " | " + this.err.printed());
	}

	@Test
	public void replacesEachNumberedPlaceholderByItsArgument() {
		assertEquals("Welcome Ann, it's 5 o'clock, Ann!", TGMessagesManager.getProperty("welcome", new Object[] {"Ann", 5}));
		assertEquals("Welcome Ann, it's null o'clock, Ann!", TGMessagesManager.getProperty("welcome", new Object[] {"Ann", null}));
	}

	@Test
	public void readsTheMessageAsIsWithoutArguments() {
		assertEquals(WELCOME, TGMessagesManager.getProperty("welcome", new Object[0]));
		assertEquals(WELCOME, TGMessagesManager.getProperty("welcome", null));
	}

	@Test
	public void readsTheMessageAsIsWhenAnArgumentHasNoText() {
		Object noText = new Object() {
			@Override
			public String toString() {
				return null;
			}
		};

		assertEquals(WELCOME, TGMessagesManager.getProperty("welcome", new Object[] {"Ann", noText}));
	}

	// instruments are the messages midi.instrument-0, -1, ... up to the first one missing
	@Test
	public void instrumentsAreReadByIndexUpToTheFirstMissingOne() {
		Properties properties = new Properties();
		properties.setProperty("midi.instrument-0", "Piano");
		properties.setProperty("midi.instrument-1", "Organ");
		properties.setProperty("midi.instrument-3", "Unreachable");
		TGMessagesManager.getInstance().setResources(new TGResourceBundle(Locale.ROOT, properties));

		assertEquals("Piano", TGMessagesManager.getMidiInstrument(0).getName());
		assertEquals("Organ", TGMessagesManager.getMidiInstrument(1).getName());
		assertNull(TGMessagesManager.getMidiInstrument(2));
		assertNull(TGMessagesManager.getMidiInstrument(-1));
		assertEquals(2, TGMessagesManager.getMidiInstrumentsNumber());
	}
}
