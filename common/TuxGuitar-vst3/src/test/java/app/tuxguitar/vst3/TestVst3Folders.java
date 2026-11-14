package app.tuxguitar.vst3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/**
 * The folders VST3 plugins are installed in on each system (the VST3 SDK's "Plug-in Locations"), searched in the order
 * the SDK lists them. On Windows they are read from the environment; a variable missing leaves its folder out.
 */
class TestVst3Folders {

	private static final File HOME = new File("/home/me");
	private static final Map<String, String> WINDOWS = Map.of("COMMONPROGRAMFILES", "/common", "LOCALAPPDATA", "/local");

	// the folders on the system with the environment, joined by ", ", with "/" between names
	private static String folders(String os, Map<String, String> env) {
		return TGVst3Folders.standard(os, env, HOME).stream().map(folder -> folder.getPath().replace(File.separatorChar, '/')).collect(Collectors.joining(", "));
	}

	@Test
	void eachSystemsFoldersInTheOrderTheSdkListsThem() {
		assertEquals("Windows: /common/VST3, /local/Programs/Common/VST3"
			+ " | macOS: /Library/Audio/Plug-Ins/VST3, /home/me/Library/Audio/Plug-Ins/VST3"
			+ " | Linux: /home/me/.vst3, /usr/lib/vst3, /usr/local/lib/vst3",
			"Windows: " + folders("Windows 11", WINDOWS) + " | macOS: " + folders("Mac OS X", WINDOWS) + " | Linux: " + folders("Linux", WINDOWS));
	}

	@Test
	void onWindowsAFolderWhoseVariableIsMissingIsLeftOut() {
		assertEquals("/local/Programs/Common/VST3 | /common/VST3 | ", folders("Windows 10", Map.of("LOCALAPPDATA", "/local")) + " | " + folders("Windows 10", Map.of("COMMONPROGRAMFILES", "/common")) + " | " + folders("Windows 10", Collections.<String, String>emptyMap()));
	}
}
