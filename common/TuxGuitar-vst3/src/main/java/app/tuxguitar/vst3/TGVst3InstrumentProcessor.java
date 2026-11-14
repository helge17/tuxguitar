package app.tuxguitar.vst3;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.midi.synth.TGMidiProcessor;

/**
 * A VST3 instrument as the source of a sound chain: the notes sent to it are played in the next block, each on its
 * frame (an untimed note on the block's first), with its velocity. A note is known by its voice (the string it is
 * played on) and its key, so two notes of one key on different strings (unison) are two notes the instrument stops and
 * tunes apart; a note of no voice by its key. A bend of a voice tunes the note that voice last started, alone, by its
 * exact semitones; a bend of no voice, or one without semitones, is not played.
 */
public final class TGVst3InstrumentProcessor extends TGVst3Processor implements TGMidiProcessor {

	// the voices a note can be played on, and its keys: MIDI's 128 each
	private static final int VOICES = 128;
	private static final int KEYS = 128;
	private static final int NONE = -1;

	private final List<TGVst3NoteEvent> events = new ArrayList<TGVst3NoteEvent>();
	// by voice, the key of the note it last started; NONE before
	private final int[] notes = new int[VOICES];

	public TGVst3InstrumentProcessor(TGVst3Hosting hosting) {
		super(hosting, true);
		java.util.Arrays.fill(this.notes, NONE);
	}

	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) {
		this.sendNoteOn(key, velocity, voice, bendMode, 0);
	}

	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode, long frame) {
		synchronized (this.events) {
			if (isVoice(voice)) {
				this.notes[voice] = key;
			}
			this.events.add(TGVst3NoteEvent.noteOn((int) frame, key, noteId(key, voice), velocity / 127f));
		}
	}

	private static boolean isVoice(int voice) {
		return (voice >= 0 && voice < VOICES);
	}

	// the note's id: by its voice and key (a note of no voice: by its key alone)
	private static int noteId(int key, int voice) {
		return (isVoice(voice) ? (voice + 1) * KEYS : 0) + key;
	}

	// the tuning of the note the voice last started, on the frame
	public void sendPitchBend(int value, double semitones, int voice, boolean bendMode, long frame) {
		synchronized (this.events) {
			int note = (isVoice(voice) ? this.notes[voice] : NONE);
			if (note != NONE) {
				this.events.add(TGVst3NoteEvent.tuning((int) frame, noteId(note, voice), (float) semitones));
			}
		}
	}

	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) {
		this.sendNoteOff(key, velocity, voice, bendMode, 0);
	}

	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode, long frame) {
		this.add(TGVst3NoteEvent.noteOff((int) frame, key, noteId(key, voice)));
	}

	public void sendPitchBend(int value, int voice, boolean bendMode) {
	}

	public void sendControlChange(int controller, int value) {
	}

	private void add(TGVst3NoteEvent event) {
		synchronized (this.events) {
			this.events.add(event);
		}
	}

	List<TGVst3NoteEvent> takeEvents() {
		synchronized (this.events) {
			List<TGVst3NoteEvent> taken = new ArrayList<TGVst3NoteEvent>(this.events);
			this.events.clear();
			return taken;
		}
	}
}
