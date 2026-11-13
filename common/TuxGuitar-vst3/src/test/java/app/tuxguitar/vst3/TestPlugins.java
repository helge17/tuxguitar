package app.tuxguitar.vst3;

import java.io.File;

/**
 * Where the native build (native/build.cmd or build.sh, run by Maven before the tests) put the host process and the test
 * plugins: the system property tg.vst3.native names its directory.
 */
public final class TestPlugins {

	private TestPlugins() {
	}

	public static File nativeBuild() {
		String directory = System.getProperty("tg.vst3.native");
		if (directory == null) {
			throw new IllegalStateException("tg.vst3.native is not set: run the tests through Maven, which builds the VST3 host first");
		}
		return new File(directory);
	}

	// the host executable, with or without the Windows extension
	public static File host() {
		File bin = new File(nativeBuild(), "bin");
		File windows = new File(bin, "tuxguitar-vst3-host.exe");
		return (windows.isFile() ? windows : new File(bin, "tuxguitar-vst3-host"));
	}

	// the folder holding the test plugins' bundles
	public static File folder() {
		return new File(new File(nativeBuild(), "VST3"), "Release");
	}

	public static File bundle(String name) {
		return new File(folder(), name + ".vst3");
	}

	// a block of frames, each side at a constant level
	public static float[][] block(int frames, float left, float right) {
		float[][] block = new float[2][frames];
		java.util.Arrays.fill(block[0], left);
		java.util.Arrays.fill(block[1], right);
		return block;
	}

	// ends the process, as a crash would, and waits until it is gone
	public static void kill(long pid) {
		ProcessHandle process = ProcessHandle.of(pid).get();
		process.destroyForcibly();
		process.onExit().join();
	}

	// the folder holding Old Amp, a VST 2 plugin
	public static File vst2Folder() {
		return new File(nativeBuild(), "vst2");
	}
}
