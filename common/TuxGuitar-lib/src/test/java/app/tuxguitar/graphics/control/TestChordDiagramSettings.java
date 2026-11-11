package app.tuxguitar.graphics.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import app.tuxguitar.ui.resource.UIColor;

/**
 * Pins when changing a setting of a chord invalidates its buffered diagram (the TGChordImpl setters): only when the
 * value changes. A color is the same when it has the same red, green and blue and neither was released; a missing
 * color only matches a missing one. Fonts are pinned by {@link TestChordFontChange}.
 *
 * Each check builds a chord on a beat of a guitar track, applies the setting, buffers the diagram, then applies the new setting; a result lists
 * the settings that released the diagram.
 */
public class TestChordDiagramSettings {

	private final FakeResourceFactory factory = new FakeResourceFactory();
	private final LayoutHarness harness = new LayoutHarness();

	private static final Map<String, BiConsumer<TGChordImpl, Integer>> NUMBERS = new LinkedHashMap<String, BiConsumer<TGChordImpl, Integer>>();
	static {
		NUMBERS.put("tonic", (chord, value) -> chord.setTonic(value));
		NUMBERS.put("firstFretSpacing", (chord, value) -> chord.setFirstFretSpacing(value));
		NUMBERS.put("fretSpacing", (chord, value) -> chord.setFretSpacing(value));
		NUMBERS.put("stringSpacing", (chord, value) -> chord.setStringSpacing(value));
		NUMBERS.put("noteSize", (chord, value) -> chord.setNoteSize(value));
		NUMBERS.put("lineWidth", (chord, value) -> chord.setLineWidth(value));
		NUMBERS.put("firstFret", (chord, value) -> chord.setFirstFret(value));
		NUMBERS.put("fret", (chord, value) -> chord.addFretValue(2, value));
	}

	private static final Map<String, BiConsumer<TGChordImpl, UIColor>> COLORS = new LinkedHashMap<String, BiConsumer<TGChordImpl, UIColor>>();
	static {
		COLORS.put("foreground", TGChordImpl::setForegroundColor);
		COLORS.put("background", TGChordImpl::setBackgroundColor);
		COLORS.put("color", TGChordImpl::setColor);
		COLORS.put("note", TGChordImpl::setNoteColor);
		COLORS.put("tonic", TGChordImpl::setTonicColor);
	}

	private boolean released(Consumer<TGChordImpl> setUp, Consumer<TGChordImpl> change) {
		TGChordImpl chord = new TGChordImpl(6);
		chord.setBeat(harness.beatAt(0));
		setUp.accept(chord);
		chord.updateDiagram(factory, new TGResourceBuffer());
		assertFalse(chord.isDisposed());
		change.accept(chord);
		return chord.isDisposed();
	}

	// the settings that release the diagram when changed from one value to another
	private <T> String releasing(Map<String, BiConsumer<TGChordImpl, T>> settings, T from, T to) {
		StringBuilder released = new StringBuilder();
		for (Map.Entry<String, BiConsumer<TGChordImpl, T>> setting : settings.entrySet()) {
			if (released(chord -> setting.getValue().accept(chord, from), chord -> setting.getValue().accept(chord, to))) {
				released.append(released.length() == 0 ? "" : " ").append(setting.getKey());
			}
		}
		return released.toString();
	}

	private String colorsReleasing(UIColor from, UIColor to) {
		return releasing(COLORS, from, to);
	}

	private UIColor rgb(int red, int green, int blue) {
		return factory.createColor(red, green, blue);
	}

	private UIColor released(UIColor color) {
		color.dispose();
		return color;
	}

	private static final String ALL_COLORS = "foreground background color note tonic";

	@Test
	public void aNumberReleasesTheDiagramOnlyWhenChanged() {
		assertEquals("", releasing(NUMBERS, 3, 3));
		assertEquals("tonic firstFretSpacing fretSpacing stringSpacing noteSize lineWidth firstFret fret", releasing(NUMBERS, 3, 4));
	}

	@Test
	public void anEqualColorKeepsTheDiagram() {
		assertEquals("", colorsReleasing(rgb(10, 20, 30), rgb(10, 20, 30)));
		assertEquals("", colorsReleasing(null, null));
	}

	@Test
	public void anotherColorReleasesTheDiagram() {
		assertEquals(ALL_COLORS, colorsReleasing(rgb(10, 20, 30), rgb(11, 20, 30)));
		assertEquals(ALL_COLORS, colorsReleasing(rgb(10, 20, 30), rgb(10, 21, 30)));
		assertEquals(ALL_COLORS, colorsReleasing(rgb(10, 20, 30), rgb(10, 20, 31)));
	}

	@Test
	public void aMissingColorOnlyMatchesAMissingOne() {
		assertEquals(ALL_COLORS, colorsReleasing(null, rgb(10, 20, 30)));
		assertEquals(ALL_COLORS, colorsReleasing(rgb(10, 20, 30), null));
	}

	@Test
	public void aReleasedColorNeverMatches() {
		assertEquals(ALL_COLORS, colorsReleasing(released(rgb(10, 20, 30)), rgb(10, 20, 30)));
		assertEquals(ALL_COLORS, colorsReleasing(rgb(10, 20, 30), released(rgb(10, 20, 30))));
	}
}
