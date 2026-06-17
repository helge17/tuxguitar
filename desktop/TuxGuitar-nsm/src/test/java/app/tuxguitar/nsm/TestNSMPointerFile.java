package app.tuxguitar.nsm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class TestNSMPointerFile {

	@TempDir
	Path tempDir;

	@Test
	public void testWriteAndReadRealPath() {
		String sessionDir = tempDir.toString();
		NSMPointerFile.write(sessionDir, "/home/user/songs/my-song.tg");
		assertEquals("/home/user/songs/my-song.tg", NSMPointerFile.read(sessionDir));
	}

	@Test
	public void testWriteNullMeansNewSession() {
		String sessionDir = tempDir.toString();
		NSMPointerFile.write(sessionDir, null);
		assertNull(NSMPointerFile.read(sessionDir));
	}

	@Test
	public void testWriteEmptyStringMeansNewSession() {
		String sessionDir = tempDir.toString();
		NSMPointerFile.write(sessionDir, "");
		assertNull(NSMPointerFile.read(sessionDir));
	}

	@Test
	public void testReadMissingFile() {
		assertNull(NSMPointerFile.read(tempDir.toString()));
	}

	@Test
	public void testReadFromNonexistentDirectory() {
		assertNull(NSMPointerFile.read(tempDir.toString() + File.separator + "no-such-dir"));
	}

	@Test
	public void testOverwriteUpdatesValue() {
		String sessionDir = tempDir.toString();
		NSMPointerFile.write(sessionDir, "/first/path.tg");
		NSMPointerFile.write(sessionDir, "/second/path.tg");
		assertEquals("/second/path.tg", NSMPointerFile.read(sessionDir));
	}

	@Test
	public void testPointerFileName() {
		assertEquals("tuxguitar-source.path", NSMPointerFile.FILE_NAME);
	}

	@Test
	public void testPathWithSpaces() {
		String sessionDir = tempDir.toString();
		String path = "/home/user/my songs/my song.tg";
		NSMPointerFile.write(sessionDir, path);
		assertEquals(path, NSMPointerFile.read(sessionDir));
	}
}
