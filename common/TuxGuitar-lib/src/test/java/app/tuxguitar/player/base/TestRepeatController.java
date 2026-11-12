package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import app.tuxguitar.io.tg.TestFileFormat20;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

public class TestRepeatController {

	private TGSong song;

	// load test song
	public TestRepeatController() throws IOException {
		this.song = new TestFileFormat20().readSong("altRepeatLoop_20.tg", true).getSong();
	}

	@Test
	public void testNominalSequence() {
		checkSequence(new MidiRepeatController(this.song, -1, -1),
				new int[] {1,2,3, 1, 4,5, 1, 4,5, 1, 6,7,  8,9, 8,9, 8,9, 10,  11,11, 12,12, 13},
				new int[] {0,0,0, 3, 1,1, 6, 4,4, 9, 5,5,  5,5, 7,7, 9,9,  9,   9,10, 10,11, 11});
	}

	@Test
	public void testLoop1() {
		checkSequence(new MidiRepeatController(this.song, 1, 1),
				new int[] {1},
				new int[] {0});
	}

	@Test
	public void testLoop123() {
		checkSequence(new MidiRepeatController(this.song, 1, 3),
				new int[] {1,2,3},
				new int[] {0,0,0});
	}

	@Test
	public void testLooPAltRepeat2_3() {
		checkSequence(new MidiRepeatController(this.song, 4, 5),
				new int[] {4,5},
				new int[] {0,0});
	}

	@Test
	public void testLooPAltRepeat4() {
	checkSequence(new MidiRepeatController(this.song, 6, 7),
				new int[] {6,7},
				new int[] {0,0});
	}

	// https://github.com/helge17/tuxguitar/issues/1146
	@Test
	public void testConsecutiveRepeats() {
		checkSequence(new MidiRepeatController(this.song, 11, 12),
				new int[] {11,11, 12,12},
				new int[] {0,1, 1,2});
	}

	private void checkSequence(MidiRepeatController controller, int[] expectedMeasures, int[] expectedMoveNbMeasures) {
		int i=0;
		while (!controller.finished()) {
			int index = controller.getIndex();
			long move = controller.getRepeatMove();
			controller.process();
			if (controller.shouldPlay()) {
				if (i>=expectedMeasures.length) {
					System.out.printf("unexpected: %d\n", index+1);
					fail("too long sequence");
				}
				assertEquals(expectedMeasures[i], index+1, "unexpected measure");
				// *2*quarter_time: because time signature of test file is 2/4 (2 quarters in each measure)
				assertEquals(expectedMoveNbMeasures[i]*2*TGDuration.QUARTER_TIME, move, "unexpected move");
				i++;
			}
		}
		assertEquals(i,expectedMeasures.length, "too short sequence");
	}

	// ---- hand-made songs of 4/4 measures ----

	// a new song with one measure per mark: "|:" opens a repeat, ":|" closes it once, "2." plays on the second
	// pass only, "-" is a plain measure
	private static TGSong song(String... marks) {
		TGSongManager songManager = new TGSongManager();
		TGSong song = songManager.newSong();
		for (int i = 1; i < marks.length; i++) {
			songManager.addNewMeasureBeforeEnd(song);
		}
		for (int i = 0; i < marks.length; i++) {
			TGMeasureHeader header = song.getMeasureHeader(i);
			header.setRepeatOpen(marks[i].equals("|:"));
			header.setRepeatClose(marks[i].equals(":|") ? 1 : 0);
			header.setRepeatAlternative(marks[i].equals("2.") ? 2 : 0);
		}
		return song;
	}

	// the measures played, each as "number@move", the move in measures
	private static String played(TGSong song, int sHeader, int eHeader) {
		MidiRepeatController controller = new MidiRepeatController(song, sHeader, eHeader);
		long measureLength = song.getMeasureHeader(0).getLength();
		StringBuilder played = new StringBuilder();
		while (!controller.finished()) {
			int index = controller.getIndex();
			long move = controller.getRepeatMove();
			controller.process();
			if (controller.shouldPlay()) {
				played.append(played.length() == 0 ? "" : " ").append(index + 1).append("@").append(move / measureLength);
			}
		}
		return played.toString();
	}

	private static DynamicTest plays(String name, String expected, int sHeader, int eHeader, String... marks) {
		return DynamicTest.dynamicTest(name, () -> assertEquals(expected, played(song(marks), sHeader, eHeader)));
	}

	@TestFactory
	public Stream<DynamicTest> handMadeSongs() {
		return Stream.of(
			// a repeat closed in a loop goes back to the loop start
			plays("a loop start opens a repeat", "2@0 3@0 2@2 3@2 4@2", 2, 4, "-", "-", ":|", "-"),
			// a later repeat close without a new repeat open is ignored
			plays("a finished repeat stays closed", "1@0 2@0 1@2 2@2 3@2 4@2", -1, -1, "|:", ":|", ":|", "-"),
			// looks odd, pinned as is: a second ending right after the repeat open is skipped on the first pass, and
			// so is the repeat close after it, so the repeat is never played again
			plays("a second ending is skipped on the first pass", "1@0 4@-2", -1, -1, "|:", "2.", ":|", "-"),
			plays("a repeat before the loop does not count in it", "2@0 3@0 2@2 3@2 4@2", 2, 4, ":|", "-", ":|", "-"),
			plays("a range ending at 0 plays nothing", "", -1, 0, "-", "-"));
	}
}
