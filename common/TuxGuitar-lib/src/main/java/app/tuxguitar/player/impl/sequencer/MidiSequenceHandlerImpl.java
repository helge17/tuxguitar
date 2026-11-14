package app.tuxguitar.player.impl.sequencer;

import java.util.function.Consumer;

import app.tuxguitar.player.base.MidiSequenceHandler;
import app.tuxguitar.song.models.TGTimeSignature;

public class MidiSequenceHandlerImpl extends MidiSequenceHandler{

	// where the events go, and the time signatures
	private final Consumer<MidiEvent> events;
	private final Consumer<MidiEvent> signatures;

	public MidiSequenceHandlerImpl(MidiSequencerImpl seq,int tracks) {
		this(tracks, seq::addEvent, seq::addTimeSignature);
		seq.getMidiTrackController().init(getTracks());
	}

	private MidiSequenceHandlerImpl(int tracks, Consumer<MidiEvent> events, Consumer<MidiEvent> signatures) {
		super(tracks);
		this.events = events;
		this.signatures = signatures;
	}

	// the events of the sequencer's next pass of a loop, time signatures among them (its tracks stay as they are)
	static MidiSequenceHandlerImpl nextPass(MidiSequencerImpl seq, int tracks) {
		return new MidiSequenceHandlerImpl(tracks, seq::addNextPassEvent, seq::addNextPassEvent);
	}

	public void addControlChange(long tick,int track,int channel, int controller, int value) {
		this.events.accept(MidiEvent.controlChange(tick, track, channel, controller, value));
	}

	public void addNoteOff(long tick,int track,int channel, int note, int velocity, int voice, boolean bendMode) {
		this.events.accept(MidiEvent.noteOff(tick, track, channel, note, velocity, voice, bendMode));
	}

	public void addNoteOn(long tick,int track,int channel, int note, int velocity, int voice, boolean bendMode) {
		this.events.accept(MidiEvent.noteOn(tick, track, channel, note, velocity, voice, bendMode));
	}

	public void addPitchBend(long tick,int track,int channel, int value, int voice, boolean bendMode) {
		this.addPitchBend(tick, track, channel, value, MidiSequenceHandler.semitones(value), voice, bendMode);
	}

	public void addPitchBend(long tick,int track,int channel, int value, double semitones, int voice, boolean bendMode) {
		this.events.accept(MidiEvent.pitchBend(tick, track, channel, value, semitones, voice, bendMode));
	}

	public void addProgramChange(long tick,int track,int channel, int instrument) {
		this.events.accept(MidiEvent.programChange(tick, track, channel, instrument));
	}

	public void addTrackName(long tick, int track, String name) {
		this.events.accept(MidiEvent.trackName(tick, track, name));
	}

	public void addTempoInUSQ(long tick,int track,int usq) {
		this.events.accept(MidiEvent.tempoInUSQ(tick, usq));
	}

	public void addTimeSignature(long tick,int track,TGTimeSignature ts) {
		this.signatures.accept(MidiEvent.timeSignature(tick, track, ts.getNumerator(), ts.getDenominator().getValue()));
	}

	public void notifyFinish(){
		//not implemented
	}
}
