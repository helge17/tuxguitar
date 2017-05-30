package org.herac.tuxguitar.midi.synth.impl;

public class GervillProgram {
	
	public static final int CHANNEL_MODE_SINGLE = 1;
	public static final int CHANNEL_MODE_BEND = 2;
	public static final int CHANNEL_MODE_VOICE = 3;
	
	private int bank;
	private int program;
	private int channelMode;
	private String soundbankPath;
	
	public GervillProgram() {
		this.bank = 0;
		this.program = -1;
		this.channelMode = CHANNEL_MODE_BEND;
	}

	public int getBank() {
		return this.bank;
	}

	public void setBank(int bank) {
		this.bank = bank;
	}

	public int getProgram() {
		return this.program;
	}

	public void setProgram(int program) {
		this.program = program;
	}

	public int getChannelMode() {
		return this.channelMode;
	}

	public void setChannelMode(int channelMode) {
		this.channelMode = channelMode;
	}

	public String getSoundbankPath() {
		return this.soundbankPath;
	}

	public void setSoundbankPath(String soundbankPath) {
		this.soundbankPath = soundbankPath;
	}
	
	public void copyFrom(GervillProgram program) {
		this.bank = program.bank;
		this.program = program.program;
		this.channelMode = program.channelMode;
		this.soundbankPath = program.soundbankPath;
	}
	
	public boolean equals(Object obj) {
		if( obj instanceof GervillProgram ) {
			GervillProgram p = (GervillProgram) obj;
			if( this.bank != p.bank ) {
				return false;
			}
			if( this.program != p.program ) {
				return false;
			}
			if( this.channelMode != p.channelMode ) {
				return false;
			}
			if((this.soundbankPath != null ? this.soundbankPath : "").equals((p.soundbankPath != null ? p.soundbankPath : ""))) {
				return false;
			}
			return true;
		}
		return false;
	}
}
