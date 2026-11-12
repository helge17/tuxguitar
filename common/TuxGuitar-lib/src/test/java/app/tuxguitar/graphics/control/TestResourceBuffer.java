package app.tuxguitar.graphics.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import app.tuxguitar.ui.resource.UIColor;

/**
 * Pins the resource buffer the layouts keep images and colors in (TGResourceBuffer): a resource is kept by key
 * until replaced or disposed; disposing the unregistered resources keeps only the ones registered since the
 * registry was last cleared.
 */
public class TestResourceBuffer {

	private final TGResourceBuffer buffer = new TGResourceBuffer();
	private final FakeResourceFactory resources = new FakeResourceFactory();

	private UIColor keep(String key) {
		UIColor color = resources.createColor(1, 2, 3);
		buffer.setResource(key, color);
		return color;
	}

	@Test
	public void aResourceIsKeptByKeyUntilReplaced() {
		UIColor first = keep("a");
		UIColor second = keep("a");

		assertSame(second, buffer.getResource("a"));
		assertTrue(first.isDisposed());
		assertFalse(second.isDisposed());
		assertNull(buffer.getResource("b"));
		assertTrue(buffer.isResourceDisposed("b"));
	}

	@Test
	public void disposingUnregisteredResourcesKeepsTheRegisteredOnes() {
		UIColor registered = keep("registered");
		UIColor unregistered = keep("unregistered");
		UIColor droppedFromRegistry = keep("dropped");
		buffer.register("registered");
		buffer.register("registered");
		buffer.register("dropped");
		buffer.unregister("dropped");

		buffer.disposeUnregisteredResources();

		assertEquals("registered kept, unregistered disposed, dropped disposed",
			state("registered", registered) + ", " + state("unregistered", unregistered) + ", " + state("dropped", droppedFromRegistry));
	}

	@Test
	public void clearingTheRegistryLetsEveryResourceBeDisposed() {
		UIColor color = keep("a");
		buffer.register("a");
		buffer.clearRegistry();

		buffer.disposeUnregisteredResources();

		assertEquals("a disposed", state("a", color));
	}

	@Test
	public void disposingAllResourcesEmptiesTheBuffer() {
		UIColor a = keep("a");
		UIColor b = keep("b");
		buffer.register("a");

		buffer.disposeAllResources();

		assertEquals("a disposed, b disposed", state("a", a) + ", " + state("b", b));
	}

	// whether the resource is still in the buffer under its key, or was disposed and dropped
	private String state(String key, UIColor resource) {
		boolean kept = (buffer.getResource(key) == resource && !resource.isDisposed());
		boolean disposed = (buffer.getResource(key) == null && resource.isDisposed());
		return key + (kept ? " kept" : disposed ? " disposed" : " inconsistent");
	}
}
