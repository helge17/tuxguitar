package app.tuxguitar.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Pins TGKeyBindFormatter: without a translator a key combination reads as its keys joined by "+"; a translator,
 * once set, decides alone.
 */
public class TestKeyBindFormatter {

	private final TGKeyBindFormatter formatter = TGKeyBindFormatter.getInstance();

	@AfterEach
	public void removeTranslator() {
		formatter.setTranslator(null);
	}

	@Test
	public void joinsTheKeysWithPlus() {
		assertEquals("Ctrl+Shift+S", formatter.format(List.of("Ctrl", "Shift", "S")));
		assertEquals("F1", formatter.format(List.of("F1")));
		assertEquals("", formatter.format(List.of()));
	}

	@Test
	public void aTranslatorFormatsInstead() {
		formatter.setTranslator(keys -> String.join(" ", keys).toLowerCase());

		assertEquals("ctrl s", formatter.format(List.of("Ctrl", "S")));
	}
}
