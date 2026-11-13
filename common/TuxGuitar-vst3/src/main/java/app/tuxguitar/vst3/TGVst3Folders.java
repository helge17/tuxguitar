package app.tuxguitar.vst3;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The VST3 modules in folders: every file or bundle named *.vst3, in any subfolder (a bundle's own content is not
 * searched), folder by folder, in name order.
 */
public final class TGVst3Folders {

	private static final String EXTENSION = ".vst3";

	private TGVst3Folders() {
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
