package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import app.tuxguitar.util.TGContext;

/**
 * Pins how the player sounds a beat by itself, outside of a song being played (MidiPlayer.playBeat with a duration and
 * an interval): each note is turned on, one interval after the other, held for the duration, then all are turned off.
 * A failure stops the beat silently.
 *
 * The beat is played on channel 2. It first sets the song's channels up; only the notes that reach the player's
 * output are written here, as MidiReceiverRecorder writes them.
 */
public class TestMidiPlayerBeat {

	private static final int[][] CHORD = {{60, 90}, {64, 80}, {67, 70}};
	private static final String CHORD_NOTES = "on 2 60 90 -1 0, on 2 64 80 -1 0, on 2 67 70 -1 0, "
			+ "off 2 60 90 -1 0, off 2 64 80 -1 0, off 2 67 70 -1 0";

	private final List<String> received = new ArrayList<String>();
	private final MidiPlayer player = MidiPlayer.getInstance(new TGContext());

	public TestMidiPlayerBeat() {
		player.getOutputTransmitter().addReceiver("recorder", new MidiReceiverRecorder(received));
	}

	// plays the chord: the notes that reached the output
	private String playChord(long duration, int interval) {
		received.clear();
		player.playBeat(2, 0, 25, 100, 64, 0, 0, 0, 0, CHORD, duration, interval);
		return received.stream().filter(call -> call.matches("(on|off) .*")).collect(Collectors.joining(", "));
	}

	private static boolean tookAtLeast(long millis, long startNanos) {
		return (System.nanoTime() - startNanos) >= millis * 1000000;
	}

	@Test
	public void aBeatTurnsItsNotesOnOneIntervalApartHoldsThemThenTurnsThemOff() {
		long start = System.nanoTime();
		String held = playChord(60, 0) + " | " + tookAtLeast(60, start);
		start = System.nanoTime();
		// three notes, three intervals of 40 ms, then 1 ms held
		String spaced = playChord(1, 40) + " | " + tookAtLeast(120, start);

		assertEquals(CHORD_NOTES + " | true / " + CHORD_NOTES + " | true", held + " / " + spaced);
	}

	@Test
	public void aFailureStopsTheBeatSilently() {
		player.getOutputTransmitter().addReceiver("broken", new MidiReceiverRecorder(new ArrayList<String>()) {
			public void sendNoteOn(int channel, int key, int velocity, int voice, boolean bendMode) {
				throw new IllegalStateException("broken");
			}
		});

		assertEquals("on 2 60 90 -1 0", playChord(60, 0));
	}
}
