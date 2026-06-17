package app.tuxguitar.nsm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestNSMUrl {

	@Test
	public void testValidUrl() throws Exception {
		NSMUrl url = new NSMUrl("osc.udp://127.0.0.1:12345");
		assertEquals(12345, url.port);
		assertEquals("127.0.0.1", url.address.getHostAddress());
	}

	@Test
	public void testUrlWithTrailingSlash() throws Exception {
		NSMUrl url = new NSMUrl("osc.udp://127.0.0.1:9000/");
		assertEquals(9000, url.port);
		assertEquals("127.0.0.1", url.address.getHostAddress());
	}

	@Test
	public void testLocalhostUrl() throws Exception {
		NSMUrl url = new NSMUrl("osc.udp://localhost:7171");
		assertEquals(7171, url.port);
		assertNotNull(url.address);
	}

	@Test
	public void testHighPortNumber() throws Exception {
		NSMUrl url = new NSMUrl("osc.udp://127.0.0.1:65535");
		assertEquals(65535, url.port);
	}

	@Test
	public void testInvalidScheme() {
		assertThrows(IllegalArgumentException.class, () -> new NSMUrl("osc.tcp://127.0.0.1:1234"));
	}

	@Test
	public void testMissingScheme() {
		assertThrows(IllegalArgumentException.class, () -> new NSMUrl("127.0.0.1:1234"));
	}

	@Test
	public void testUrlWithLeadingWhitespace() throws Exception {
		NSMUrl url = new NSMUrl("  osc.udp://127.0.0.1:8888  ");
		assertEquals(8888, url.port);
	}
}
