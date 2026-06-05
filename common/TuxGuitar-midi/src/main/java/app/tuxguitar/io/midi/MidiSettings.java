package app.tuxguitar.io.midi;

import app.tuxguitar.player.base.MidiSequenceParser;
import app.tuxguitar.song.models.TGDuration;

public class MidiSettings {

	public static final int DEFAULT_MAX_DURATION = TGDuration.SIXTEENTH;
	public static final int DEFAULT_MAX_DIVISION = 1;
	public static final boolean DEFAULT_QUANTIZATION = true;

	private int transpose;
	private int flags;
	private boolean quantization;
	private int maxDurationValue;	// min duration
	private int maxDivision;

	public MidiSettings(){
		this.transpose = 0;
		this.flags = MidiSequenceParser.DEFAULT_EXPORT_FLAGS;
		this.quantization = DEFAULT_QUANTIZATION;
		this.maxDurationValue = DEFAULT_MAX_DURATION;
		this.maxDivision = DEFAULT_MAX_DIVISION;
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

	public boolean getQuantization() {
		return this.quantization;
	}

	public void setQuantization(boolean quantization) {
		this.quantization = quantization;
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
