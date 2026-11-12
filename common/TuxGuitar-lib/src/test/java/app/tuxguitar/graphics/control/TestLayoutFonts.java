package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontAlignment;
import app.tuxguitar.ui.resource.UIFontModel;

/**
 * Pins how a layout makes the fonts of its styles (TGResources, loaded by TGLayout.loadStyles): each style font
 * scaled by the layout's font scale, its height rounded and at least 1, its name, weight, slant and alignment kept
 * (the alignment scaled too); a font the styles leave unset is a default font.
 *
 * The layout is printed at scale 2.
 */
public class TestLayoutFonts {

	private final LayoutHarness harness = new LayoutHarness();

	public TestLayoutFonts() {
		harness.scale = 2f;
	}

	@Test
	public void aStyleFontIsScaledAndKeepsItsNameWeightSlantAndAlignment() {
		harness.styles.setDefaultFont(new UIFontModel("Serif", 10.3f, true, false, new UIFontAlignment(1, 2, 3)));
		harness.styles.setNoteFont(new UIFontModel("Mono", 7f, false, true));

		TGResources resources = harness.update(TAB).getResources();

		assertEquals("Serif font(21.0,bold,aligned 2.0/4.0/6.0) | Mono font(14.0,italic)",
			describe(resources.getDefaultFont()) + " | " + describe(resources.getNoteFont()));
	}

	@Test
	public void aTinyFontIsOneHigh() {
		harness.styles.setLyricFont(new UIFontModel("Sans", 0.2f, false, false));

		assertEquals("Sans font(1.0)", describe(harness.update(TAB).getResources().getLyricFont()));
	}

	@Test
	public void anUnsetFontIsADefaultFont() {
		assertEquals(describe(new FakeResourceFactory().createFont(new UIFontModel())), describe(harness.update(TAB).getResources().getTextFont()));
	}

	private static String describe(UIFont font) {
		return font.getName() + " " + font;
	}
}
