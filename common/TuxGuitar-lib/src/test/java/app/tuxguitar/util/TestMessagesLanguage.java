package app.tuxguitar.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import app.tuxguitar.resource.TGResourceBundle;

/**
 * Pins the language id of the loaded messages (TGMessagesManager.getLanguage): the locale's language, country and
 * variant joined by "_", an empty part leaving its separator out, except a variant without a country, which keeps
 * a double separator; null while no messages are loaded.
 */
public class TestMessagesLanguage {

	private final TGMessagesManager messages = TGMessagesManager.getInstance();

	// messages without any text: every message reads as its key, as before any messages were loaded
	@AfterEach
	public void loadNoText() {
		messages.setResources(new TGResourceBundle(Locale.ROOT, new Properties()));
	}

	// the language id of the messages loaded for each locale, " | " separated
	private String languages(Locale... locales) {
		List<String> languages = new ArrayList<String>();
		for (Locale locale : locales) {
			messages.setResources(new TGResourceBundle(locale, new Properties()));
			languages.add(messages.getLanguage());
		}
		return String.join(" | ", languages);
	}

	@Test
	public void joinsTheLanguageCountryAndVariantOfTheLocale() {
		assertEquals("fr | pt_BR | es_ES_Traditional | de__POSIX | _US | ", languages(
			new Locale("fr"),
			new Locale("pt", "BR"),
			new Locale("es", "ES", "Traditional"),
			new Locale("de", "", "POSIX"),
			new Locale("", "US"),
			Locale.ROOT));
	}

	@Test
	public void noLanguageWithoutMessages() {
		messages.setResources(null);

		assertNull(messages.getLanguage());
	}
}
