package app.tuxguitar.graphics.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import app.tuxguitar.ui.resource.UIFont;

/**
 * Pins when a new font invalidates the buffered chord diagram (TGChordImpl.setFont / setFirstFretFont): only when
 * it differs from the current one in name, height, bold or italic, when one of them is missing, or when the
 * current one was already released.
 */
public class TestChordFontChange {

	private final FakeResourceFactory factory = new FakeResourceFactory();
	private final TGChordImpl chord = new TGChordImpl(6);

	private UIFont font(String name, float height, boolean bold, boolean italic) {
		return factory.createFont(name, height, bold, italic);
	}

	private UIFont standard() {
		return font("Sans", 10f, false, false);
	}

	// buffers the diagram drawn with the given fonts
	private void drawn(UIFont font, UIFont firstFretFont) {
		chord.setFont(font);
		chord.setFirstFretFont(firstFretFont);
		chord.updateDiagram(factory, new TGResourceBuffer());
		assertFalse(chord.isDisposed());
	}

	@Test
	public void anEqualFontKeepsTheDiagram() {
		drawn(standard(), standard());

		chord.setFont(standard());
		chord.setFirstFretFont(standard());

		assertFalse(chord.isDisposed());
	}

	@Test
	public void noFontReplacedByNoFontKeepsTheDiagram() {
		drawn(null, null);

		chord.setFont(null);
		chord.setFirstFretFont(null);

		assertFalse(chord.isDisposed());
	}

	// whether replacing the font of a buffered diagram releases it
	private boolean releasedBy(UIFont from, UIFont to) {
		TGChordImpl other = new TGChordImpl(6);
		other.setFont(from);
		other.updateDiagram(factory, new TGResourceBuffer());
		other.setFont(to);
		return other.isDisposed();
	}

	@Test
	public void anotherNameHeightOrBoldReleasesTheDiagram() {
		assertTrue(releasedBy(standard(), font("Serif", 10f, false, false)));
		assertTrue(releasedBy(standard(), font("Sans", 11f, false, false)));
		assertTrue(releasedBy(standard(), font("Sans", 10f, true, false)));
	}

	@Test
	public void italicReleasesTheDiagram() {
		drawn(null, standard());
		chord.setFirstFretFont(font("Sans", 10f, false, true));
		assertTrue(chord.isDisposed());
	}

	@Test
	public void removingTheFontReleasesTheDiagram() {
		drawn(standard(), null);
		chord.setFont(null);
		assertTrue(chord.isDisposed());
	}

	@Test
	public void aFirstFontReleasesTheDiagram() {
		drawn(null, null);
		chord.setFirstFretFont(standard());
		assertTrue(chord.isDisposed());
	}

	@Test
	public void aReleasedFontIsReplacedEvenByAnEqualOne() {
		UIFont released = standard();
		drawn(released, null);
		released.dispose();

		chord.setFont(standard());

		assertTrue(chord.isDisposed());
	}

	@Test
	public void aReleasedNewFontReleasesTheDiagram() {
		drawn(standard(), null);
		UIFont released = standard();
		released.dispose();

		chord.setFont(released);

		assertTrue(chord.isDisposed());
	}
}
