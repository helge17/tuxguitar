package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.SystemErrCapture;

/**
 * Pins how the player sounds a beat by itself, outside of a song being played (MidiPlayer.playBeat): each note is
 * turned on, one interval after the other, held for the duration, then all are turned off; a failure stops the beat
 * silently. A beat of the song is played in a thread, on its track's channel, its notes held half a second; a track
 * without a channel in the song plays nothing. A failure is printed to System.err, captured here.
 *
 * The chord is played on channel 2. A beat first sets the song's channels up; only the notes that reach the player's
 * output are written here, as MidiReceiverRecorder writes them. The song has 4 measures (see PlayingFixture).
 */
public class TestMidiPlayerBeat {

	private static final int[][] CHORD = {{60, 90}, {64, 80}, {67, 70}};
	private static final String CHORD_NOTES = "on 2 60 90 -1 0, on 2 64 80 -1 0, on 2 67 70 -1 0, "
			+ "off 2 60 90 -1 0, off 2 64 80 -1 0, off 2 67 70 -1 0";

	@RegisterExtension
	final SystemErrCapture err = new SystemErrCapture();

	private final List<String> received = new ArrayList<String>();
	private final PlayingFixture fixture = new PlayingFixture();
	private final MidiPlayer player = fixture.player;

	public TestMidiPlayerBeat() {
		player.getOutputTransmitter().addReceiver("recorder", new MidiReceiverRecorder(received));
	}

	// plays the chord: the notes that reached the output
	private String playChord(long duration, int interval) {
		received.clear();
		player.playBeat(2, 0, 25, 100, 64, 0, 0, 0, 0, CHORD, duration, interval);
		return notes();
	}

	// the notes that reached the output
	private String notes() {
		return received.stream().filter(call -> call.matches("(on|off) .*")).collect(Collectors.joining(", "));
	}

	private TGNote note(int string, int fret) {
		TGNote note = fixture.songManager.getFactory().newNote();
		note.setString(string);
		note.setValue(fret);
		return note;
	}

	private static boolean tookAtLeast(long millis, long startNanos) {
		return (System.nanoTime() - startNanos) >= millis * 1000000;
	}

	@Test
	public void aBeatTurnsItsNotesOnOneIntervalApartHoldsThemThenTurnsThemOff() {
		long start = System.nanoTime();
		String held = playChord(60, 0) + " | " + tookAtLeast(50, start);
		start = System.nanoTime();
		// three notes, three intervals of 40 ms, then 1 ms held; waits may end a timer tick early, hence the margins
		String spaced = playChord(1, 40) + " | " + tookAtLeast(100, start);

		assertEquals(CHORD_NOTES + " | true / " + CHORD_NOTES + " | true", held + " / " + spaced);
	}

	@Test
	public void aBeatOfTheSongSoundsItsNotesOnItsTrackChannelInAThreadForHalfASecond() {
		TGBeat beat = fixture.beat(1);
		beat.getVoice(0).addNote(note(1, 3));
		beat.getVoice(1).addNote(note(2, 0));
		player.playBeat(beat);
		String handed = fixture.threads.pending();
		long start = System.nanoTime();
		fixture.threads.runStarted();
		String played = notes() + " | " + tookAtLeast(450, start);
		beat.getMeasure().getTrack().setChannelId(99);
		player.playBeat(beat);

		assertEquals("1 started, 0 looped / on 1 67 95 -1 0, on 1 59 95 -1 0, off 1 67 95 -1 0, off 1 59 95 -1 0 | true"
				+ " / 0 started, 0 looped", handed + " / " + played + " / " + fixture.threads.pending());
	}

	@Test
	public void aFailureStopsTheBeatSilently() {
		player.getOutputTransmitter().addReceiver("broken", new MidiReceiverRecorder(new ArrayList<String>()) {
			public void sendNoteOn(int channel, int key, int velocity, int voice, boolean bendMode) {
				throw new IllegalStateException("broken");
			}
		});

		assertEquals("on 2 60 90 -1 0 | java.lang.IllegalStateException: broken", playChord(60, 0) + " | " + err.reported());
	}
}
