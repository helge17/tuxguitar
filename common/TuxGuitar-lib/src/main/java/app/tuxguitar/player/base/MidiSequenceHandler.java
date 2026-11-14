package app.tuxguitar.player.base;

import app.tuxguitar.song.models.TGTimeSignature;

public abstract class MidiSequenceHandler {

	private int tracks;

	public MidiSequenceHandler(int tracks) {
		this.tracks = tracks;
	}

	public int getTracks() {
		return this.tracks;
	}

	public abstract void addNoteOn(long tick, int track, int channel, int note, int velocity, int voice, boolean bendMode);

	public abstract void addNoteOff(long tick, int track, int channel, int note, int velocity, int voice, boolean bendMode);

	// a pitch bend as its 7-bit value (64 none; the pitch bend range is 12 semitones, see MidiSequenceParser)
	public abstract void addPitchBend(long tick, int track, int channel, int value, int voice, boolean bendMode);

	// a pitch bend as its exact semitones too, which its 7-bit value only comes close to: what the parser sends. A
	// handler that cannot use them keeps the 7-bit value
	public void addPitchBend(long tick, int track, int channel, int value, double semitones, int voice, boolean bendMode) {
		this.addPitchBend(tick, track, channel, value, voice, bendMode);
	}

	// the semitones a 7-bit pitch bend value plays in the pitch bend range of 12 semitones: for a handler given the value
	// alone
	public static double semitones(int value) {
		return (value - 64) * 12 / 64.0;
	}

	public abstract void addControlChange(long tick, int track, int channel, int controller, int value);

	public abstract void addProgramChange(long tick, int track, int channel, int instrument);

	public abstract void addTempoInUSQ(long tick, int track, int usq);

	public abstract void addTrackName(long tick, int track, String name);

	public abstract void addTimeSignature(long tick, int track, TGTimeSignature ts);

	public abstract void notifyFinish();
}
