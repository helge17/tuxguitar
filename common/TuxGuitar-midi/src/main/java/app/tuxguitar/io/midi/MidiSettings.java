package app.tuxguitar.io.midi;

import app.tuxguitar.player.base.MidiSequenceParser;
import app.tuxguitar.song.models.TGDuration;

public class MidiSettings {

	private int transpose;
	private int flags;
	private int maxDurationValue;	// min duration
	private int maxDivision;

	public MidiSettings(){
		this.transpose = 0;
		this.flags = MidiSequenceParser.DEFAULT_EXPORT_FLAGS;
		this.maxDurationValue = TGDuration.SIXTEENTH;
		this.maxDivision = 3; // triplet
	}

	public int getTranspose() {
		return this.transpose;
	}

	public void setTranspose(int transpose) {
		this.transpose = transpose;
	}

	public int getFlags() {
		return this.flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int getMaxDurationValue() {
		return this.maxDurationValue;
	}

	public void setMaxDurationValue(int value) {
		this.maxDurationValue = value;
	}

	public int getMaxDivision() {
		return this.maxDivision;
	}

	public void setMaxDivision(int maxDivision) {
		this.maxDivision = maxDivision;
	}

	public static MidiSettings getDefaults(){
		return new MidiSettings();
	}
}
