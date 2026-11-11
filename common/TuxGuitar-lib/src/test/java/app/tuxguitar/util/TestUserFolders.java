package app.tuxguitar.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pins how TuxGuitar picks its folders. The home folder (TGUserFileUtils.PATH_HOME) is the "tuxguitar.home.path"
 * system property when it names a folder, else the "user.dir" one, else the current folder ("."). The default user
 * folders (for config and cache) are "tuxguitar" in the macOS Library sub-folder, in the Windows roaming app data,
 * or elsewhere in the XDG base directory: the XDG_*_HOME variable when set, else its default folder in the home.
 */
public class TestUserFolders {

	@TempDir
	File folder;

	private String path(String name) {
		return new File(this.folder, name).getAbsolutePath();
	}

	@Test
	public void takesTheFirstPathThatIsAFolder() throws IOException {
		new File(this.folder, "home").mkdir();
		new File(this.folder, "user").mkdir();
		new File(this.folder, "file").createNewFile();

		assertEquals(path("home"), TGUserFileUtils.getHomePath(path("home"), path("user")));
		assertEquals(path("user"), TGUserFileUtils.getHomePath(null, path("user")));
		assertEquals(path("user"), TGUserFileUtils.getHomePath(path("missing"), path("user")));
		assertEquals(path("user"), TGUserFileUtils.getHomePath(path("file"), path("user")));
	}

	@Test
	public void fallsBackToTheCurrentFolder() {
		String current = new File(".").getAbsolutePath();
		assertEquals(current, TGUserFileUtils.getHomePath(null, null));
		assertEquals(current, TGUserFileUtils.getHomePath(path("missing"), path("missing")));
	}

	private static String join(String... names) {
		return String.join(File.separator, names);
	}

	@Test
	public void theDefaultUserFolderDependsOnTheSystem() {
		assertEquals(join("home", "Library", "Caches", "tuxguitar"),
			TGUserFileUtils.getDefaultUserDir("home", "Mac OS X", "xdg", "Caches", ".cache"));
		assertEquals(join("home", "AppData", "Roaming", "tuxguitar"),
			TGUserFileUtils.getDefaultUserDir("home", "Windows 11", "xdg", "Caches", ".cache"));
		assertEquals(join("xdg", "tuxguitar"),
			TGUserFileUtils.getDefaultUserDir("home", "Linux", "xdg", "Caches", ".cache"));
		assertEquals(join("home", ".cache", "tuxguitar"),
			TGUserFileUtils.getDefaultUserDir("home", "Linux", " ", "Caches", ".cache"));
		assertEquals(join("home", ".cache", "tuxguitar"),
			TGUserFileUtils.getDefaultUserDir("home", "Mac OS", null, "Caches", ".cache"));
	}

	@Test
	public void theHomeOfThisRunIsTheUserFolder() {
		// tests run without tuxguitar.home.path
		assertEquals(null, System.getProperty("tuxguitar.home.path"));
		assertEquals(new File(System.getProperty("user.dir")).getAbsolutePath(), TGUserFileUtils.PATH_HOME);
	}
}
