package app.tuxguitar.vst3;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The VST3 modules in folders: every file or bundle named *.vst3, in any subfolder (a bundle's own content is not
 * searched), folder by folder, in name order.
 */
public final class TGVst3Folders {

	private static final String EXTENSION = ".vst3";

	private TGVst3Folders() {
	}

	/**
	 * The folders VST3 plugins are installed in on the system (os: its name, as os.name says it), in the order the VST3
	 * SDK lists them: on Windows the common files' and the user's programs' (from the environment; a variable missing
	 * leaves its folder out), on macOS the system's and the user's library, elsewhere the user's and the system's.
	 */
	public static List<File> standard(String os, Map<String, String> env, File home) {
		String system = os.toLowerCase(Locale.ROOT);
		if (system.startsWith("windows")) {
			List<File> folders = new ArrayList<File>();
			under(folders, env.get("COMMONPROGRAMFILES"), "VST3");
			under(folders, env.get("LOCALAPPDATA"), "Programs/Common/VST3");
			return folders;
		}
		if (system.startsWith("mac")) {
			return Arrays.asList(new File("/Library/Audio/Plug-Ins/VST3"), new File(home, "Library/Audio/Plug-Ins/VST3"));
		}
		return Arrays.asList(new File(home, ".vst3"), new File("/usr/lib/vst3"), new File("/usr/local/lib/vst3"));
	}

	// the path under the folder, unless there is no folder
	private static void under(List<File> folders, String folder, String path) {
		if (folder != null) {
			folders.add(new File(folder, path));
		}
	}

	public static List<File> modules(List<File> folders) {
		List<File> modules = new ArrayList<File>();
		for (File folder : folders) {
			collect(folder, modules);
		}
		return modules;
	}

	private static void collect(File folder, List<File> modules) {
		File[] children = folder.listFiles();
		if (children == null) {
			return;
		}
		Arrays.sort(children);
		for (File child : children) {
			if (child.getName().toLowerCase(Locale.ROOT).endsWith(EXTENSION)) {
				modules.add(child);
			} else if (child.isDirectory()) {
				collect(child, modules);
			}
		}
	}
}
