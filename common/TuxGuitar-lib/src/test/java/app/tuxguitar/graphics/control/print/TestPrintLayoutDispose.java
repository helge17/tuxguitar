package app.tuxguitar.graphics.control.print;

import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.graphics.control.LayoutHarness;
import app.tuxguitar.ui.resource.UIFont;

/**
 * Pins TGPrintLayout.disposeLayout: the header fonts are released, and the next print creates them again.
 */
public class TestPrintLayoutDispose {

	private final LayoutHarness harness = new LayoutHarness();
	private final TGPrintLayout layout = harness.update(TAB);

	private List<UIFont> headerFonts() {
		return Arrays.asList(layout.getSongNameFont(), layout.getSongAuthorFont(), layout.getTrackNameFont(),
			layout.getArtistFont(), layout.getAlbumNameYearFont());
	}

	@Test
	public void releasesEveryHeaderFont() {
		List<UIFont> fonts = headerFonts();

		layout.disposeLayout();

		for (UIFont font : fonts) {
			assertTrue(font.isDisposed(), font.getName() + " " + font.getHeight());
		}
	}

	@Test
	public void theNextPrintCreatesTheFontsAgain() {
		List<UIFont> fonts = headerFonts();
		layout.disposeLayout();

		List<UIFont> again = headerFonts();

		for (int i = 0; i < fonts.size(); i++) {
			assertNotSame(fonts.get(i), again.get(i));
			assertFalse(again.get(i).isDisposed());
		}
	}

	@Test
	public void disposingBeforeTheHeaderFontsExistLeavesThemToBeCreated() {
		layout.disposeLayout();

		for (UIFont font : headerFonts()) {
			assertFalse(font.isDisposed());
		}
	}
}
