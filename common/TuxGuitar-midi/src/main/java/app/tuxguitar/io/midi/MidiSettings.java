package app.tuxguitar.io.midi;

import app.tuxguitar.player.base.MidiSequenceParser;

public class MidiSettings {

	private int transpose;
	private int flags;

	public MidiSettings(){
		this.transpose = 0;
		this.flags = MidiSequenceParser.DEFAULT_EXPORT_FLAGS;
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

	public static MidiSettings getDefaults(){
		return new MidiSettings();
	}
}
