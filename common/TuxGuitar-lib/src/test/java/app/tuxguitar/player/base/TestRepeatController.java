package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import app.tuxguitar.io.tg.TestFileFormat20;
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
				new int[] {1,2,3, 1, 4,5, 1, 4,5, 1, 6,7,  8,9, 8,9, 8,9, 10},
				new int[] {0,0,0, 3, 1,1, 6, 4,4, 9, 5,5,  5,5, 7,7, 9,9,  9});
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
}
