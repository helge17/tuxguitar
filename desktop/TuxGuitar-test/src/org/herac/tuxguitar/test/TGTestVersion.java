package org.herac.tuxguitar.test;

import org.herac.tuxguitar.util.TGVersion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TGTestVersion {
	
	private void assertIsInvalid(TGVersion version) {
		assertEquals(0, version.getMajor());
		assertEquals(0, version.getMinor());
		assertEquals(0, version.getRevision());
	}
	
	@Test
	public void versionFromString() {
		TGVersion version = new TGVersion("1.2.3");
		assertEquals(1, version.getMajor());
		assertEquals(2, version.getMinor());
		assertEquals(3, version.getRevision());
		
		version = new TGVersion("3.4");
		assertEquals(3, version.getMajor());
		assertEquals(4, version.getMinor());
		assertEquals(0, version.getRevision());
		
		assertIsInvalid(new TGVersion(null));
		assertIsInvalid(new TGVersion(""));
		assertIsInvalid(new TGVersion("1,2,3"));
		assertIsInvalid(new TGVersion("1"));
		assertIsInvalid(new TGVersion("1?2.3"));
		assertIsInvalid(new TGVersion("1.2.-3"));
		assertIsInvalid(new TGVersion("1.a.3"));
		assertIsInvalid(new TGVersion("4.5.6.1"));
	}
	
	@Test
	public void ordering() {
		assertTrue(new TGVersion(1,2,3).compareTo(new TGVersion("")) > 0);
		assertTrue(new TGVersion("").compareTo(new TGVersion(1,2,3)) < 0);

		assertTrue(new TGVersion(0,1,0).compareTo(new TGVersion("abc")) > 0);
		assertTrue(new TGVersion("abc").compareTo(new TGVersion(0,1,0)) < 0);

		assertTrue(new TGVersion(0,1,0).compareTo(new TGVersion("0.0.1")) > 0);
		assertTrue(new TGVersion("0.0.1").compareTo(new TGVersion(0,1,0)) < 0);

		assertTrue(new TGVersion(2,3,4).compareTo(new TGVersion("2.3.3")) > 0);
		assertTrue(new TGVersion("2.3.3").compareTo(new TGVersion(2,3,4)) < 0);

		assertTrue(new TGVersion(2,3,4).compareTo(new TGVersion("2.2.5")) > 0);
		assertTrue(new TGVersion("2.2.5").compareTo(new TGVersion(2,3,4)) < 0);

		assertTrue(new TGVersion(2,3,4).compareTo(new TGVersion("1.4.5")) > 0);
		assertTrue(new TGVersion("1.4.5").compareTo(new TGVersion(2,3,4)) < 0);

		assertTrue(new TGVersion(2,3,4).compareTo(new TGVersion("2.3")) > 0);
		assertTrue(new TGVersion("2.3").compareTo(new TGVersion(2,3,4)) < 0);
		
		assertEquals(0, new TGVersion(2,3,4).compareTo(new TGVersion("2.3.4")));
	}
	
}
