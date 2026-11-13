package app.tuxguitar.player.base;

import app.tuxguitar.song.models.TGVelocities;

/**
 * The click sound: the General MIDI drum note and the velocity that Count In and the Metronome play. A TuxGuitar
 * setting (the player's), not part of the song. Immutable: a note from 0 to 127 at a velocity from 1 to 127.
 */
public final class MidiClick {

	// Side Stick at the default velocity, the click TuxGuitar has always played
	public static final MidiClick DEFAULT = new MidiClick(37, TGVelocities.DEFAULT);

	private static final int MAX = 127;

	private final int note;
	private final int velocity;

	public MidiClick(int note, int velocity) {
		if (note < 0 || note > MAX || velocity < 1 || velocity > MAX) {
			throw new IllegalArgumentException("not a MIDI note and velocity: " + note + ", " + velocity);
		}
		this.note = note;
		this.velocity = velocity;
	}

	public int getNote() {
		return this.note;
	}

	public int getVelocity() {
		return this.velocity;
	}
}
