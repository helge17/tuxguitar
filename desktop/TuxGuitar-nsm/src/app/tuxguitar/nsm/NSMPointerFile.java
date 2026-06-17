package app.tuxguitar.nsm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Reads and writes the {@code tuxguitar-source.path} pointer file stored in
 * the NSM session directory.  The file records the real path of the document
 * that was open when the session was last saved, so it can be re-opened on
 * the next session restore even if it lives outside the session directory.
 */
class NSMPointerFile {

	static final String FILE_NAME = "tuxguitar-source.path";

	static void write(String sessionDir, String realPath) {
		File file = new File(sessionDir, FILE_NAME);
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			pw.println(realPath != null ? realPath : "");
			pw.close();
		} catch (Exception e) {
			System.err.println("[NSM] failed to write pointer file: " + e);
		}
	}

	static String read(String sessionDir) {
		File file = new File(sessionDir, FILE_NAME);
		if (!file.exists()) {
			return null;
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line = br.readLine();
			br.close();
			return (line != null && !line.trim().isEmpty()) ? line.trim() : null;
		} catch (Exception e) {
			System.err.println("[NSM] failed to read pointer file: " + e);
			return null;
		}
	}
}
