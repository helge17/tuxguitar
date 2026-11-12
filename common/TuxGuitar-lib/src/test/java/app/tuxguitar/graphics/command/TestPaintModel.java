package app.tuxguitar.graphics.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Pins the bounds of a paint model (TGPaintModel): the outermost bound of its commands on each side, whichever
 * command holds it (the first one on a tie), and 0 on every side when it has no commands.
 */
public class TestPaintModel {

	// x: 1, -2, 4, 0 / y: 3, 5, -1, 2 -- each extreme held by a command in the middle
	private final TGPaintModel model = new TGPaintModel(
		new TGMoveTo(1, 3),
		new TGLineTo(-2, 5),
		new TGLineTo(4, -1),
		new TGMoveTo(0, 2)
	);

	@Test
	public void eachSideIsTheOutermostBoundOfItsCommands() {
		assertEquals("x -2.0..4.0 y -1.0..5.0", bounds(model));
	}

	@Test
	public void theSizeSpansTheBounds() {
		assertEquals("6.0x6.0", model.getWidth() + "x" + model.getHeight());
	}

	@Test
	public void aCommandSpanningSeveralPointsCountsWithItsOuterPoints() {
		TGPaintModel withRectangle = new TGPaintModel(new TGMoveTo(0, 0), new TGRectangle(1, -3, 2, 10));

		assertEquals("x 0.0..3.0 y -3.0..7.0", bounds(withRectangle));
	}

	@Test
	public void onATieTheFirstCommandsBoundIsKept() {
		// -0.0 and 0.0 are equal bounds, told apart only by their sign
		TGPaintModel tied = new TGPaintModel(new TGMoveTo(-0f, -0f), new TGMoveTo(0f, 0f));

		assertEquals("x -0.0..-0.0 y -0.0..-0.0", bounds(tied));
	}

	@Test
	public void withoutCommandsEverySideIsZero() {
		assertEquals("x 0.0..0.0 y 0.0..0.0", bounds(new TGPaintModel()));
	}

	private static String bounds(TGPaintModel model) {
		return "x " + model.getMinimumX() + ".." + model.getMaximumX() + " y " + model.getMinimumY() + ".." + model.getMaximumY();
	}
}
