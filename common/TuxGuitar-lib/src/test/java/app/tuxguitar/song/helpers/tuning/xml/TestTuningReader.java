package app.tuxguitar.song.helpers.tuning.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.tuxguitar.song.helpers.tuning.TuningGroup;
import app.tuxguitar.song.helpers.tuning.TuningPreset;

/**
 * Pins how TuningReader.loadTunings fills a group from a tunings XML document: nested groups,
 * tunings with their notes and optional numeric priority. Reading stops at the first invalid entry, and the
 * error is printed (to System.err, captured here), not thrown: what was read before it is kept, except the
 * groups still open around it.
 *
 * The group is read back one line per tuning: "group/path: name [notes] priority";
 * the printed error is read back as its first line.
 */
public class TestTuningReader {

	private static final String INVALID_FORMAT = "java.lang.RuntimeException: Invalid Tuning file format.";

	private final ByteArrayOutputStream printed = new ByteArrayOutputStream();
	private PrintStream systemErr;

	@BeforeEach
	public void captureErrors() {
		this.systemErr = System.err;
		System.setErr(new PrintStream(this.printed, true));
	}

	@AfterEach
	public void restoreErrors() {
		System.setErr(this.systemErr);
	}

	private TuningGroup read(String body) {
		return load(new TuningGroup(), stream("<?xml version=\"1.0\"?><tunings>" + body + "</tunings>"));
	}

	private TuningGroup load(TuningGroup group, InputStream stream) {
		this.printed.reset();
		new TuningReader().loadTunings(group, stream);
		return group;
	}

	private static InputStream stream(String xml) {
		return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
	}

	// the first line of each printed error, without its stack trace
	private String error() {
		return Arrays.stream(new String(this.printed.toByteArray(), StandardCharsets.UTF_8).split("\r?\n"))
			.filter(line -> !line.isEmpty())
			.filter(line -> !line.startsWith("\t") && !line.startsWith("Caused by"))
			.collect(Collectors.joining("\n"));
	}

	private static String describe(TuningGroup group) {
		StringBuilder sb = new StringBuilder();
		describe(group, "", sb);
		return sb.toString();
	}

	private static void describe(TuningGroup group, String path, StringBuilder sb) {
		for (TuningPreset tuning : group.getTunings()) {
			assertSame(group, tuning.getParent());
			sb.append(path).append(": ").append(tuning.getName()).append(' ').append(Arrays.toString(tuning.getValues()))
				.append(' ').append(tuning.getPriority()).append('\n');
		}
		for (TuningGroup child : group.getGroups()) {
			assertSame(group, child.getParent());
			describe(child, path + "/" + child.getName(), sb);
		}
	}

	@Test
	public void readsNestedGroupsOfTunings() {
		TuningGroup root = read(""
			+ "<tuning name=\"Top\" notes=\"60\"/>"
			+ "<group name=\"Guitar\">"
			+ "  <group name=\"6-String\">"
			+ "    <tuning name=\"E Tuning\" notes=\"64,59,55,50,45,40\" priority=\"1\"/>"
			+ "    <tuning name=\"Drop D\" notes=\"64,59,55,50,45,38\"/>"
			+ "  </group>"
			+ "  <tuning name=\"Low\" notes=\"0,127\"/>"
			+ "</group>"
			+ "<group name=\"\"/>"
			+ "<!-- ignored --><other name=\"x\"/>");

		assertEquals(""
			+ ": Top [60] null\n"
			+ "/Guitar: Low [0, 127] null\n"
			+ "/Guitar/6-String: E Tuning [64, 59, 55, 50, 45, 40] 1\n"
			+ "/Guitar/6-String: Drop D [64, 59, 55, 50, 45, 38] null\n"
			, describe(root));
		// a group's name may be blank
		assertEquals(2, root.getGroups().size());
		assertEquals("", root.getGroups().get(1).getName());
		assertEquals("", error());
	}

	@Test
	public void keepsOnlyAPriorityMadeOfDigits() {
		TuningGroup root = read(""
			+ "<tuning name=\"a\" notes=\"60\" priority=\"12\"/>"
			+ "<tuning name=\"b\" notes=\"60\" priority=\"\"/>"
			+ "<tuning name=\"c\" notes=\"60\" priority=\"-1\"/>"
			+ "<tuning name=\"d\" notes=\"60\" priority=\"high\"/>");

		assertEquals(": a [60] 12\n: b [60] null\n: c [60] null\n: d [60] null\n", describe(root));
		assertEquals("", error());
	}

	@Test
	public void stopsAtANoteOutOfTheMidiRange() {
		assertEquals(": ok [60] null\n", describe(read("<tuning name=\"ok\" notes=\"60\"/><tuning name=\"high\" notes=\"60,128\"/><tuning name=\"after\" notes=\"60\"/>")));
		assertEquals("java.lang.RuntimeException: Invalid Tuning note: 128", error());

		assertEquals(": ok [60] null\n", describe(read("<tuning name=\"ok\" notes=\"60\"/><tuning name=\"low\" notes=\"-1\"/>")));
		assertEquals("java.lang.RuntimeException: Invalid Tuning note: -1", error());

		assertEquals(": ok [60] null\n", describe(read("<tuning name=\"ok\" notes=\"60\"/><tuning name=\"nan\" notes=\"6x\"/>")));
		assertEquals("java.lang.NumberFormatException: For input string: \"6x\"", error());
	}

	@Test
	public void stopsAtABlankOrMissingNameOrNotes() {
		for (String invalid : new String[] {
			"<tuning name=\" \" notes=\"60\"/>",
			"<tuning name=\"a\" notes=\"\"/>",
			"<tuning notes=\"60\"/>",
			"<tuning name=\"a\"/>",
			"<group/>"}) {
			assertEquals("", describe(read(invalid)), invalid);
			assertEquals(INVALID_FORMAT, error(), invalid);
		}
	}

	@Test
	public void dropsTheGroupsOpenAroundAnInvalidTuning() {
		TuningGroup root = read(""
			+ "<group name=\"Done\"><tuning name=\"a\" notes=\"60\"/></group>"
			+ "<group name=\"Outer\"><group name=\"Inner\">"
			+ "  <tuning name=\"b\" notes=\"60\"/><tuning name=\"bad\" notes=\"999\"/>"
			+ "</group></group>");

		assertEquals("/Done: a [60] null\n", describe(root));
		assertEquals("java.lang.RuntimeException: Invalid Tuning note: 999", error());
	}

	@Test
	public void readsNothingFromNoStream() {
		assertEquals("", describe(load(new TuningGroup(), null)));
		assertEquals("", error());
	}

	@Test
	public void readsNothingFromABrokenDocument() {
		assertEquals("", describe(load(new TuningGroup(), stream("<tunings><tuning"))));
		assertEquals("org.xml.sax.SAXParseException; lineNumber: 1; columnNumber: 17; "
			+ "XML document structures must start and end within the same entity.", error());
	}

	@Test
	public void refusesADocumentType() {
		TuningGroup root = load(new TuningGroup(), stream("<!DOCTYPE tunings [<!ENTITY x \"y\">]><tunings><tuning name=\"&x;\" notes=\"60\"/></tunings>"));

		assertEquals("", describe(root));
		assertTrue(error().startsWith("org.xml.sax.SAXParseException; lineNumber: 1; columnNumber: 10; DOCTYPE is disallowed"), error());
	}

	@Test
	public void readsTheShippedTunings() throws Exception {
		TuningGroup root;
		try (InputStream stream = new FileInputStream("../resources/tunings/tunings.xml")) {
			root = load(new TuningGroup(), stream);
		}

		String tunings = describe(root);
		assertTrue(tunings.startsWith("/Guitar/6-String: E Tuning [64, 59, 55, 50, 45, 40] 1\n"), tunings);
		assertEquals("", error());
	}
}
