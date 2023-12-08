package org.herac.tuxguitar.player.impl.jsa.message;

import javax.sound.midi.ShortMessage;

public class MidiShortMessage extends ShortMessage{
	
	public static final int DEFAULT_VOICE = -1;
	public static final boolean DEFAULT_BEND_MODE = false;
	
	private int channel;
	private int voice;
	private boolean bendMode;
	
	public MidiShortMessage(){
		super();
	}
	
	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getVoice() {
		return this.voice;
	}

	public void setVoice(int voice) {
		this.voice = voice;
	}

	public boolean isBendMode() {
		return this.bendMode;
	}

	public void setBendMode(boolean bendMode) {
		this.bendMode = bendMode;
	}
}
