package app.tuxguitar.app.view.dialog.measure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TGMeasureCopyDialogRegressionTest {

	@Test
	public void clampFromSelection_shouldClampToMinimum() {
		assertEquals(1, TGMeasureCopyDialog.clampFromSelection(0, 5, 1));
	}

	@Test
	public void clampFromSelection_shouldClampToToSelection() {
		assertEquals(4, TGMeasureCopyDialog.clampFromSelection(9, 4, 1));
	}

	@Test
	public void clampFromSelection_shouldKeepValidSelection() {
		assertEquals(3, TGMeasureCopyDialog.clampFromSelection(3, 6, 1));
	}

	@Test
	public void clampToSelection_shouldClampToFromSelection() {
		assertEquals(3, TGMeasureCopyDialog.clampToSelection(2, 3, 10));
	}

	@Test
	public void clampToSelection_shouldClampToMaximum() {
		assertEquals(10, TGMeasureCopyDialog.clampToSelection(11, 3, 10));
	}

	@Test
	public void clampToSelection_shouldKeepValidSelection() {
		assertEquals(7, TGMeasureCopyDialog.clampToSelection(7, 3, 10));
	}

	@Test
	public void isFirstMeasureButtonEnabled_shouldBeFalseAtFirstMeasure() {
		assertFalse(TGMeasureCopyDialog.isFirstMeasureButtonEnabled(1));
	}

	@Test
	public void isFirstMeasureButtonEnabled_shouldBeTrueAfterFirstMeasure() {
		assertTrue(TGMeasureCopyDialog.isFirstMeasureButtonEnabled(2));
	}

	@Test
	public void isLastMeasureButtonEnabled_shouldBeFalseAtLastMeasure() {
		assertFalse(TGMeasureCopyDialog.isLastMeasureButtonEnabled(10, 10));
	}

	@Test
	public void isLastMeasureButtonEnabled_shouldBeTrueBeforeLastMeasure() {
		assertTrue(TGMeasureCopyDialog.isLastMeasureButtonEnabled(9, 10));
	}
}
