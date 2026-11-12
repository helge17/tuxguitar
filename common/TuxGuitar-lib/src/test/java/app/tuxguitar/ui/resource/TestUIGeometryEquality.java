package app.tuxguitar.ui.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Pins equality of positions, sizes and rectangles: equal when every coordinate matches; never equal to another
 * type or to null. A rectangle without a position or size reads as zero.
 */
public class TestUIGeometryEquality {

	@Test
	public void positionsAreEqualWhenBothCoordinatesMatch() {
		UIPosition position = new UIPosition(1, 2);

		assertEquals(position, new UIPosition(1, 2));
		assertEquals(position, position.clone());
		assertNotEquals(position, new UIPosition(9, 2));
		assertNotEquals(position, new UIPosition(1, 9));
		assertNotEquals(position, "position");
	}

	@Test
	public void sizesAreEqualWhenWidthAndHeightMatch() {
		UISize size = new UISize(3, 4);

		assertEquals(size, new UISize(3, 4));
		assertEquals(size, size.clone());
		assertNotEquals(size, new UISize(9, 4));
		assertNotEquals(size, new UISize(3, 9));
		assertNotEquals(size, new UIPosition(3, 4));
	}

	@Test
	public void rectanglesAreEqualWhenPositionAndSizeMatch() {
		UIRectangle rectangle = new UIRectangle(1, 2, 3, 4);

		assertEquals(rectangle, new UIRectangle(1, 2, 3, 4));
		assertEquals(rectangle, rectangle.clone());
		assertNotEquals(rectangle, new UIRectangle(9, 2, 3, 4));
		assertNotEquals(rectangle, new UIRectangle(1, 2, 3, 9));
		assertNotEquals(rectangle, rectangle.getSize());
		assertNotEquals(rectangle, null);
	}

	@Test
	public void aRectangleReadsItsBoundsAndTreatsMissingPositionOrSizeAsZero() {
		UIRectangle rectangle = new UIRectangle(1, 2, 3, 4);
		UIRectangle empty = new UIRectangle(null, null);

		assertEquals("1.0 2.0 3.0 4.0", rectangle.getX() + " " + rectangle.getY() + " " + rectangle.getWidth() + " " + rectangle.getHeight());
		assertEquals("0.0 0.0 0.0 0.0", empty.getX() + " " + empty.getY() + " " + empty.getWidth() + " " + empty.getHeight());
	}
}
