package app.tuxguitar.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import app.tuxguitar.util.error.TGErrorManager;

/**
 * Pins the order in which TGLibraryLoader.loadLibraries loads the native libraries of a folder: the files whose
 * name starts with the prefix and ends with the extension; a library listed in another's "<name>.deps" file (one
 * name per line) is loaded first, when it is in the folder. Folders are skipped.
 *
 * The libraries here are not real native code, so each load fails and is reported to the error handlers: the
 * reported errors give the loading order. A "<name>.deps" that cannot be read is reported too, and lists nothing.
 */
public class TestLibraryLoader {

	@TempDir
	File folder;

	private final TGContext context = new TGContext();
	private final List<String> reported = new ArrayList<String>();

	@BeforeEach
	public void recordErrors() {
		TGErrorManager.getInstance(this.context).addErrorHandler(throwable -> this.reported.add(describe(throwable)));
	}

	// the error type and the name of the file it is about (its message starts with the file path)
	private String describe(Throwable throwable) {
		String message = throwable.getMessage();
		String name = message.substring(message.indexOf(this.folder.getName()) + this.folder.getName().length() + 1);
		return throwable.getClass().getSimpleName() + " " + name.split("[: ]")[0];
	}

	private void file(String name, String... lines) throws IOException {
		Files.write(new File(this.folder, name).toPath(), String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
	}

	private List<String> load(File folder, String prefix, String extension) {
		TGLibraryLoader.getInstance(this.context).loadLibraries(folder, prefix, extension);
		return this.reported;
	}

	@Test
	public void loadsTheDependenciesOfALibraryFirst() throws IOException {
		file("libA.so");
		file("libB.so");
		file("libC.so");
		file("libA.so.deps", "libC.so", "libMissing.so");
		file("libC.so.deps", " libB.so ");

		assertEquals(List.of("UnsatisfiedLinkError libB.so", "UnsatisfiedLinkError libC.so", "UnsatisfiedLinkError libA.so"),
			load(this.folder, "lib", ".so"));
	}

	@Test
	public void loadsOnlyTheFilesWithThePrefixAndExtension() throws IOException {
		file("libA.so");
		file("other.so");
		file("libB.dll");
		new File(this.folder, "libDir.so").mkdir();

		assertEquals(List.of("UnsatisfiedLinkError libA.so"), load(this.folder, "lib", ".so"));
	}

	@Test
	public void withoutPrefixOrExtensionLoadsEveryFile() throws IOException {
		file("A.so");
		load(this.folder, "", null);

		assertEquals(List.of("UnsatisfiedLinkError A.so", "UnsatisfiedLinkError A.so"), load(this.folder, null, ""));
	}

	@Test
	public void aOneLetterPrefixAndExtensionAreChecked() throws IOException {
		file("lib.o");
		file("lib.x");
		file("dll.o");

		assertEquals(List.of("UnsatisfiedLinkError lib.o"), load(this.folder, "l", "o"));
	}

	@Test
	public void anUnreadableDependencyFileListsNothing() throws IOException {
		file("libA.so");
		new File(this.folder, "libA.so.deps").mkdir();

		assertEquals(List.of("FileNotFoundException libA.so.deps", "UnsatisfiedLinkError libA.so"),
			load(this.folder, "lib", ".so"));
	}

	@Test
	public void aMissingFolderLoadsNothing() {
		assertEquals(List.of(), load(new File(this.folder, "missing"), "lib", ".so"));
		assertEquals(List.of(), load(null, "lib", ".so"));
	}
}
