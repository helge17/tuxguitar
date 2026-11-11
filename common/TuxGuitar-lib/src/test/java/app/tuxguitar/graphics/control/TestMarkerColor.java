package app.tuxguitar.graphics.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.ui.resource.UIColor;

/**
 * Pins TGMeasureImpl.getMarkerColor: the color a measure paints its marker with is made once from the marker's
 * color and kept in the resource buffer; it is made again, and the old one disposed, when the marker's color
 * changes or the kept one was disposed.
 */
public class TestMarkerColor {

	private final LayoutHarness harness = new LayoutHarness();
	private final TGResourceBuffer buffer = new TGResourceBuffer();
	private final FakeResourceFactory resources = new FakeResourceFactory();
	private final TGMarker marker = harness.factory.newMarker();

	public TestMarkerColor() {
		marker.getColor().setR(10);
		marker.getColor().setG(20);
		marker.getColor().setB(30);
		harness.measure().getHeader().setMarker(marker);
	}

	private UIColor markerColor() {
		return harness.measure().getMarkerColor(buffer, resources);
	}

	@Test
	public void theColorIsMadeFromTheMarkerAndKeptInTheBuffer() {
		UIColor color = markerColor();

		assertEquals("rgb(10,20,30)", color.toString());
		assertSame(color, buffer.getResource(harness.measure().getMarkerRegistryKey()));
		assertSame(color, markerColor());
		assertFalse(color.isDisposed());
	}

	@Test
	public void aChangeOfAnyComponentMakesANewColorAndDisposesTheOldOne() {
		assertRemadeAfter(() -> marker.getColor().setR(11), "rgb(11,20,30)");
		assertRemadeAfter(() -> marker.getColor().setG(21), "rgb(11,21,30)");
		assertRemadeAfter(() -> marker.getColor().setB(31), "rgb(11,21,31)");
	}

	@Test
	public void aDisposedColorIsMadeAgain() {
		assertRemadeAfter(() -> buffer.getResource(harness.measure().getMarkerRegistryKey()).dispose(), "rgb(10,20,30)");
	}

	private void assertRemadeAfter(Runnable change, String expected) {
		UIColor before = markerColor();
		change.run();

		UIColor after = markerColor();

		assertNotSame(before, after);
		assertTrue(before.isDisposed());
		assertEquals(expected, after.toString());
		assertSame(after, buffer.getResource(harness.measure().getMarkerRegistryKey()));
	}
}
