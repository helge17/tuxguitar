package app.tuxguitar.vst3;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.midi.synth.TGMidiProcessor;

/**
 * A VST3 instrument as the source of a sound chain: the notes sent to it are played in the next block, each on its
 * frame (an untimed note on the block's first), with its velocity. A note is known by its key.
 */
public final class TGVst3InstrumentProcessor extends TGVst3Processor implements TGMidiProcessor {

	private final List<TGVst3NoteEvent> events = new ArrayList<TGVst3NoteEvent>();

	public TGVst3InstrumentProcessor(TGVst3Hosting hosting) {
		super(hosting, true);
	}

	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) {
		this.sendNoteOn(key, velocity, voice, bendMode, 0);
	}

	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode, long frame) {
		this.add(TGVst3NoteEvent.noteOn((int) frame, key, key, velocity / 127f));
	}

	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) {
		this.sendNoteOff(key, velocity, voice, bendMode, 0);
	}

	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode, long frame) {
		this.add(TGVst3NoteEvent.noteOff((int) frame, key, key));
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
