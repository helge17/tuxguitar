package app.tuxguitar.song.helpers.tuning.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

import app.tuxguitar.song.helpers.tuning.TuningGroup;
import app.tuxguitar.song.helpers.tuning.TuningPreset;
import app.tuxguitar.util.SystemErrCapture;

/**
 * Pins how TuningWriter.write saves a group of tunings as a tunings XML file: a group's sub-groups as "group"
 * elements, then its tunings as "tuning" elements with their name and comma separated notes. A group gets a name
 * attribute only when its name is not empty; tuning priorities are not saved. TuningReader reads back what the
 * writer saved. A file that cannot be written is reported (printed), not thrown.
 *
 * The file is read back as its XML text without the declaration and without the whitespace between elements.
 */
public class TestTuningWriter {

	@TempDir
	File folder;

	@RegisterExtension
	final SystemErrCapture err = new SystemErrCapture();

	private static TuningGroup group(TuningGroup parent, String name) {
		TuningGroup group = new TuningGroup(parent, name);
		parent.addGroup(group);
		return group;
	}

	private static TuningPreset tuning(TuningGroup group, String name, int... notes) {
		TuningPreset tuning = new TuningPreset(group, name, notes);
		group.addTuningPreset(tuning);
		return tuning;
	}

	private static TuningGroup guitarTunings() {
		TuningGroup root = new TuningGroup();
		tuning(root, "Top", 60).setPriority(1);
		TuningGroup guitar = group(root, "Guitar");
		TuningGroup sixString = group(guitar, "6-String");
		tuning(sixString, "E Tuning", 64, 59, 55, 50, 45, 40);
		tuning(sixString, "Drop D", 64, 59, 55, 50, 45, 38);
		tuning(guitar, "Low", 0, 127);
		return root;
	}

	private File write(TuningGroup group, String name) {
		File file = new File(this.folder, name);
		TuningWriter.write(group, file.getPath());
		return file;
	}

	private static String xml(File file) throws IOException {
		String text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
		return text.replaceFirst("^<\\?xml[^>]*\\?>", "").replaceAll(">\\s+<", "><").trim();
	}

	private static TuningGroup read(File file) throws IOException {
		TuningGroup group = new TuningGroup();
		try (InputStream stream = new FileInputStream(file)) {
			new TuningReader().loadTunings(group, stream);
		}
		return group;
	}

	@Test
	public void writesTheSubGroupsThenTheTuningsOfEveryGroup() throws IOException {
		assertEquals("<tunings>"
			+ "<group name=\"Guitar\">"
			+ "<group name=\"6-String\">"
			+ "<tuning name=\"E Tuning\" notes=\"64,59,55,50,45,40\"/>"
			+ "<tuning name=\"Drop D\" notes=\"64,59,55,50,45,38\"/>"
			+ "</group>"
			+ "<tuning name=\"Low\" notes=\"0,127\"/>"
			+ "</group>"
			+ "<tuning name=\"Top\" notes=\"60\"/>"
			+ "</tunings>", xml(write(guitarTunings(), "tunings.xml")));
	}

	@Test
	public void writesWhatTheReaderReadsBack() throws IOException {
		File written = write(guitarTunings(), "tunings.xml");

		assertEquals(xml(written), xml(write(read(written), "again.xml")));
	}

	@Test
	public void namesOnlyTheGroupsThatHaveAName() throws IOException {
		TuningGroup root = new TuningGroup(null, "Mine");
		tuning(group(root, ""), "a", 60);
		tuning(group(root, null), "b", 61);

		assertEquals("<tunings name=\"Mine\">"
			+ "<group><tuning name=\"a\" notes=\"60\"/></group>"
			+ "<group><tuning name=\"b\" notes=\"61\"/></group>"
			+ "</tunings>", xml(write(root, "tunings.xml")));
	}

	@Test
	public void reportsAFileThatCannotBeWritten() {
		TuningWriter.write(guitarTunings(), this.folder.getPath());

		assertTrue(this.err.printed().startsWith("java.io.FileNotFoundException"), this.err.printed());
	}
}
