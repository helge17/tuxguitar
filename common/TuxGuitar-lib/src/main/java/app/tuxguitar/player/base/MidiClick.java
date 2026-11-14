package app.tuxguitar.player.base;

import app.tuxguitar.song.models.TGVelocities;
import app.tuxguitar.util.configuration.TGConfigManager;

/**
 * The click sound: the General MIDI drum note and the velocity that Count In and the Metronome play. A TuxGuitar
 * setting (the player's), not part of the song. Immutable: a note from 0 to 127 at a velocity from 1 to 127.
 */
public final class MidiClick {

	// Side Stick at the default velocity, the click TuxGuitar has always played
	public static final MidiClick DEFAULT = new MidiClick(37, TGVelocities.DEFAULT);

	// the keys it is kept under in a configuration
	public static final String NOTE = "midi.click.note";
	public static final String VELOCITY = "midi.click.velocity";

	private static final int MAX = 127;

	private final int note;
	private final int velocity;

	public MidiClick(int note, int velocity) {
		if (!isMidi(note, velocity)) {
			throw new IllegalArgumentException("not a MIDI note and velocity: " + note + ", " + velocity);
		}
		this.note = note;
		this.velocity = velocity;
	}

	// the click kept in a configuration (the app's, shared by every song); the default when none is, or when what is
	// kept is not a MIDI note and velocity (a configuration edited by hand)
	public static MidiClick read(TGConfigManager config) {
		int note = config.getIntegerValue(NOTE, DEFAULT.note);
		int velocity = config.getIntegerValue(VELOCITY, DEFAULT.velocity);
		return (isMidi(note, velocity) ? new MidiClick(note, velocity) : DEFAULT);
	}

	public void write(TGConfigManager config) {
		config.setValue(NOTE, this.note);
		config.setValue(VELOCITY, this.velocity);
	}

	private static boolean isMidi(int note, int velocity) {
		return (note >= 0 && note <= MAX && velocity >= 1 && velocity <= MAX);
	}

	public int getNote() {
		return this.note;
	}

	public int getVelocity() {
		return this.velocity;
	}
}
