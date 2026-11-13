package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.note.TGChangeNoteAction;
import app.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import app.tuxguitar.player.base.MidiChannel;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiSynthesizer;
import app.tuxguitar.thread.TGThreadHandler;
import app.tuxguitar.thread.TGThreadLoop;
import app.tuxguitar.thread.TGThreadManager;
import app.tuxguitar.thread.TGThreadPriority;

/**
 * Entering a note sounds it (TGNoteEntry, installed as the app installs it): once the note action succeeds, the
 * player plays the beat the note is on through the MIDI Port's synthesizer, on a thread of its own, unless the song is
 * playing. The synthesizer writes the notes that reach the channels it opens ("on KEY VELOCITY"); the threads the
 * player starts are kept and run here, one after the other.
 */
public class TestNoteEntry {

	private final SongActionFixture fixture = SongActionFixture.onDocument();
	private final MidiPlayer player = MidiPlayer.getInstance(this.fixture.context);
	private final List<String> sounded = new ArrayList<String>();
	private final List<Runnable> started = new ArrayList<Runnable>();

	private final class Notes implements MidiChannel {
		public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) { sounded.add("on " + key + " " + velocity); }
		public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) { sounded.add("off " + key + " " + velocity); }
		public void sendPitchBend(int value, int voice, boolean bendMode) { }
		public void sendProgramChange(int value) { }
		public void sendControlChange(int controller, int value) { }
		public void sendParameter(String key, String value) { }
		public void sendAllNotesOff() { }
	}

	private final class Synthesizer implements MidiSynthesizer {
		public MidiChannel openChannel(int channelId) { return new Notes(); }
		public void closeChannel(MidiChannel midiChannel) { }
		public boolean isChannelOpen(MidiChannel midiChannel) { return true; }
		public boolean isBusy() { return false; }
	}

	private final class KeptThreads implements TGThreadHandler {
		public void start(TGThreadPriority priority, Runnable runnable) { started.add(runnable); }
		public void loop(TGThreadLoop loop) { }
		public void yield() { }
		public void dispose() { }
		public Object getThreadId() { return null; }
	}

	public TestNoteEntry() {
		TGThreadManager.getInstance(this.fixture.context).setThreadHandler(new KeptThreads());
		this.player.getSynthesizerProxy().setMidiSynthesizer(new Synthesizer());
		TGNoteEntry.install(this.fixture.context);
	}

	// the caret on string 1 (key 64) of track 1's first beat, fret 5 typed at velocity 110
	private void caret() {
		this.fixture.caret(this.fixture.beat(this.fixture.song.getTrack(0).getMeasure(0), 0), 1);
		this.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_POSITION, this.fixture.song.getTrack(0).getMeasure(0).getStart());
		this.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, 5);
		this.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, 110);
		this.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, this.fixture.songManager.getFactory().newDuration());
	}

	// enters the note: what the player's threads sounded
	private String enter() {
		this.fixture.execute(new TGChangeNoteAction(this.fixture.context));
		return sounded();
	}

	private String sounded() {
		this.started.forEach(Runnable::run);
		return String.join(", ", this.sounded);
	}

	@Test
	public void enteringANoteSoundsItsBeat() {
		caret();

		assertEquals("on 69 110, off 69 110", enter());
	}

	@Test
	public void enteringANoteWhileTheSongPlaysSoundsNothing() {
		caret();
		this.player.setRunning(true);

		assertEquals("", enter());
	}

	// a fret past the track's last: the note action does not succeed
	@Test
	public void aNoteNotEnteredSoundsNothing() {
		caret();
		this.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, 99);

		assertEquals("", enter());
	}

	// an action that succeeds on the caret's beat but enters no note
	@Test
	public void anotherActionSoundsNothing() {
		caret();
		this.fixture.execute(new TGChangeTrackMuteAction(this.fixture.context));

		assertEquals("", sounded());
	}
}
