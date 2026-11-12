package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import app.tuxguitar.io.tg.TestFileFormat20;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGSong;
import org.junit.jupiter.api.Test;

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

	// a new song of the given number of measures
	private static TGSong song(int measures) {
		TGSongManager songManager = new TGSongManager();
		TGSong song = songManager.newSong();
		for (int i = 1; i < measures; i++) {
			songManager.addNewMeasureBeforeEnd(song);
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

	// a loop starting after the first measure opens a repeat at its start: a repeat closed in the loop goes back there
	@Test
	public void aLoopStartOpensARepeat() {
		TGSong song = song(4);
		song.getMeasureHeader(2).setRepeatClose(1);

		assertEquals("2@0 3@0 2@2 3@2 4@2", played(song, 2, 4));
	}

	// a repeat already played through is closed: a later repeat close without a new repeat open is ignored
	@Test
	public void aRepeatCloseAfterAFinishedRepeatIsIgnored() {
		TGSong song = song(4);
		song.getMeasureHeader(0).setRepeatOpen(true);
		song.getMeasureHeader(1).setRepeatClose(1);
		song.getMeasureHeader(2).setRepeatClose(1);

		assertEquals("1@0 2@0 1@2 2@2 3@2 4@2", played(song, -1, -1));
	}

	// a measure for the second ending only is skipped on the first pass, even right after the repeat open
	@Test
	public void aSecondEndingIsSkippedOnTheFirstPass() {
		TGSong song = song(4);
		song.getMeasureHeader(0).setRepeatOpen(true);
		song.getMeasureHeader(1).setRepeatAlternative(2);
		song.getMeasureHeader(2).setRepeatClose(1);

		assertEquals("1@0 4@-2", played(song, -1, -1));
	}

	// a repeat played through before the loop leaves the repeat count of the loop alone
	@Test
	public void aRepeatBeforeTheLoopDoesNotCountInIt() {
		TGSong song = song(4);
		song.getMeasureHeader(0).setRepeatClose(1);
		song.getMeasureHeader(2).setRepeatClose(1);

		assertEquals("2@0 3@0 2@2 3@2 4@2", played(song, 2, 4));
	}

	// a range ending at 0 ends before every measure
	@Test
	public void aRangeEndingAtZeroPlaysNothing() {
		assertEquals("", played(song(2), -1, 0));
	}
}
