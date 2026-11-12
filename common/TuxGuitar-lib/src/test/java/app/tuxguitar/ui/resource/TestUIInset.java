package app.tuxguitar.ui.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Pins UIInset equality: two insets are equal when their top, left, right and bottom all match; an inset is never
 * equal to an object of another type.
 */
public class TestUIInset {

	private static final UIInset INSET = new UIInset(1, 2, 3, 4);

	@Test
	public void insetsWithTheSameSidesAreEqual() {
		assertEquals(INSET, new UIInset(1, 2, 3, 4));
		assertEquals(INSET, INSET.clone());
	}

	@Test
	public void insetsDifferingOnAnySideAreNotEqual() {
		assertNotEquals(INSET, new UIInset(9, 2, 3, 4));
		assertNotEquals(INSET, new UIInset(1, 9, 3, 4));
		assertNotEquals(INSET, new UIInset(1, 2, 9, 4));
		assertNotEquals(INSET, new UIInset(1, 2, 3, 9));
	}

	@Test
	public void anInsetIsNotEqualToAnotherType() {
		assertNotEquals(INSET, "inset");
		assertNotEquals(INSET, null);
	}
}
