package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;

/**
 * Pins how the player bounds its loop (MidiPlayer.updateLoop): a complete selection wins, else the
 * loop mode's headers when they exist in the song, else no loop; the loop starts at its first header.
 *
 * The song has 4 measures; a loop is described as "start header-end header @ start position".
 */
public class TestMidiPlayerLoop {

	private final PlayerSongFixture fixture = new PlayerSongFixture();
	private final MidiPlayer player = fixture.player;

	@Test
	public void withoutLoopModeOrSelectionThereIsNoLoop() {
		assertEquals("-1--1 @ " + TGDuration.QUARTER_TIME, loop(true));
	}

	@Test
	public void theLoopModeBoundsTheLoopAndStartsItAtItsFirstHeader() {
		loopMode(2, 3);

		assertEquals("2-3 @ " + start(2), loop(true));
	}

	@Test
	public void loopModeHeadersBeyondTheSongAreIgnored() {
		loopMode(5, 4);

		assertEquals("-1-4 @ " + TGDuration.QUARTER_TIME, loop(true));
	}

	@Test
	public void aLoopFromNoHeaderStartsAtTheSongStart() {
		loopMode(-1, 3);

		assertEquals("-1-3 @ " + TGDuration.QUARTER_TIME, loop(true));
	}

	@Test
	public void aCompleteSelectionLoopsWithOrWithoutTheLoopMode() {
		player.setSelection(beat(2), beat(2));
		String withoutLoopMode = loop(true);
		loopMode(1, 4);
		player.setSelection(beat(3), beat(4));

		assertEquals("2-2 @ " + start(2) + " | 3-4 @ " + start(3), withoutLoopMode + " | " + loop(true));
	}

	@Test
	public void anIncompleteSelectionFallsBackToTheLoopMode() {
		loopMode(2, 3);
		player.setSelection(beat(4), null);
		String withLoopMode = loop(true);
		player.getMode().setLoop(false);

		assertEquals("2-3 @ " + start(2) + " | -1--1 @ " + TGDuration.QUARTER_TIME, withLoopMode + " | " + loop(true));
	}

	@Test
	public void aRunningPlayerKeepsItsLoopUnlessForced() {
		loopMode(2, 3);
		loop(true);
		loopMode(3, 4);
		player.setRunning(true);
		String kept = loop(false);
		String forced = loop(true);
		player.setRunning(false);
		String stopped = loop(false);

		assertEquals("2-3 @ " + start(2) + " | 3-4 @ " + start(3) + " | 3-4 @ " + start(3), kept + " | " + forced + " | " + stopped);
	}

	// ---- helpers ----

	private void loopMode(int from, int to) {
		fixture.loopMode(from, to);
	}

	private TGBeat beat(int measureNumber) {
		return fixture.beat(measureNumber);
	}

	private long start(int headerNumber) {
		return fixture.start(headerNumber);
	}

	private String loop(boolean force) {
		player.updateLoop(force);
		return player.getLoopSHeader() + "-" + player.getLoopEHeader() + " @ " + player.getLoopSPosition();
	}
}
