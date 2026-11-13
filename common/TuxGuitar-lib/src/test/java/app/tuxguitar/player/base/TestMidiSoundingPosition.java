package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;

/**
 * The tick a sample clock's frame is at, from the anchors its owner marks: each says from which frame the song is at
 * which tick, moving on so many ticks every so many frames. The output sounds a frame later than its events by the
 * clock's sounding delay, so the tick sounding now is the tick at the frame that many frames back.
 *
 * At 120 bpm and 44100 Hz a tick lasts 22.96875 frames: 960000000 ticks every 500000 x 44100 frames, as the sequencer
 * counts them (960 ticks a quarter note, microseconds per quarter note).
 */
public class TestMidiSoundingPosition {

	private static final double TICKS = 960000000.0;
	private static final double FRAMES = 500000 * 44100.0;

	private final MidiSoundingPosition position = new MidiSoundingPosition();

	private void anchor(long frame, long tick) {
		position.anchor(frame, tick, TICKS, FRAMES);
	}

	// the ticks at the frames, space separated
	private String at(long... frames) {
		StringBuilder ticks = new StringBuilder();
		for (long frame : frames) {
			ticks.append(ticks.length() > 0 ? " " : "").append(position.tickAt(frame));
		}
		return ticks.toString();
	}

	@Test
	public void aFrameIsAtTheTickItsNewestAnchorBeforeItReaches() {
		position.restart(0, 960, TICKS, FRAMES);
		anchor(1000, 2000);

		assertEquals("982 1003 2000 2021", at(512, 999, 1000, 1500));
	}

	// a frame before the oldest anchor: before playing started there (a pre-start position), the anchor's tempo run back
	@Test
	public void aFrameBeforeTheOldestAnchorIsBeforeItsTick() {
		position.restart(1000, 960, TICKS, FRAMES);

		assertEquals(937, position.tickAt(488));
	}

	// a jump back (the loop's) is an anchor: the frames before it are at the ticks before the jump
	@Test
	public void aJumpBackIsHeardOnItsFrame() {
		anchor(0, 4800);
		anchor(44100, 3840);

		assertEquals("6719 3840", at(44099, 44100));
	}

	// another tempo from the frame on: 60 bpm, a tick every 45.9375 frames
	@Test
	public void anAnchorCarriesItsOwnTempo() {
		position.restart(0, 960, TICKS, FRAMES);
		position.anchor(512, 982, TICKS, 2 * FRAMES);

		assertEquals(1004, position.tickAt(1536));
	}

	@Test
	public void anAnchorOnTheFrameOfTheNewestReplacesIt() {
		position.restart(0, 960, TICKS, FRAMES);
		anchor(100, 1000);
		anchor(100, 1920);

		assertEquals("964 1920", at(99, 100));
	}

	@Test
	public void aRestartForgetsTheAnchorsBeforeIt() {
		anchor(0, 960);
		position.restart(1000, 2000, TICKS, FRAMES);

		assertEquals(1956, position.tickAt(0));
	}

	// the oldest anchor kept is the one from frame 100, run back to frame 50
	@Test
	public void pastItsCapacityTheOldestAnchorsAreForgotten() {
		for (int k = 0; k <= MidiSoundingPosition.CAPACITY; k++) {
			anchor(k * 100, k * 1000);
		}

		assertEquals("997 64001", at(50, MidiSoundingPosition.CAPACITY * 100 + 23));
	}

	@Test
	public void withoutAnAnchorTheTickIsZero() {
		assertEquals(0, position.tickAt(1234));
	}

	// the owner marks and reads it on the audio thread, which must not allocate per block
	@Test
	public void markingAndReadingAllocateNothing() {
		com.sun.management.ThreadMXBean threads = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
		position.restart(0, 960, TICKS, FRAMES);
		long before = threads.getThreadAllocatedBytes(Thread.currentThread().getId());
		long sum = 0;
		for (int b = 0; b < 1000; b++) {
			anchor(b * 512, 960 + b * 22);
			sum += position.tickAt(b * 512 - 700);
		}
		long allocated = threads.getThreadAllocatedBytes(Thread.currentThread().getId()) - before;
		assertTrue(sum > 0 && allocated < 64 * 1024, allocated + " bytes allocated by 1000 blocks");
	}
}
