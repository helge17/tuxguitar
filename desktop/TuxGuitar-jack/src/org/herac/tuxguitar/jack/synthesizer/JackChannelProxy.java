package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.jack.JackPort;
import org.herac.tuxguitar.player.base.MidiChannel;
import org.herac.tuxguitar.player.base.MidiPlayerException;

public class JackChannelProxy implements MidiChannel {
	
	private int jackChannelId;
	private JackSynthesizer jackSynthesizer;
	private JackPort jackPort;
	private MidiChannel midiChannel;
	private boolean exclusive;
	
	public JackChannelProxy(int jackChannelId, JackSynthesizer jackSynthesizer) {
		this.jackChannelId = jackChannelId;
		this.jackSynthesizer = jackSynthesizer;
	}
	
	public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		if( this.midiChannel != null ){
			this.midiChannel.sendNoteOn(key, velocity, voice, bendMode);
		}
	}

	public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		if( this.midiChannel != null ){
			this.midiChannel.sendNoteOff(key, velocity, voice, bendMode);
		}
	}

	public void sendPitchBend(int value, int voice, boolean bendMode) throws MidiPlayerException {
		if( this.midiChannel != null ){
			this.midiChannel.sendPitchBend(value, voice, bendMode);
		}
	}

	public void sendProgramChange(int value) throws MidiPlayerException {
		if( this.midiChannel != null ){
			this.midiChannel.sendProgramChange(value);
		}
	}

	public void sendControlChange(int controller, int value) throws MidiPlayerException {
		if( this.midiChannel != null ){
			this.midiChannel.sendControlChange(controller, value);
		}
	}
	
	public void sendAllNotesOff() throws MidiPlayerException {
		if( this.midiChannel != null ){
			this.midiChannel.sendAllNotesOff();
		}
	}

	public void sendParameter(String key, String value) throws MidiPlayerException {
		if( JackChannelParameter.PARAMETER_EXCLUSIVE_PORT.equals(key) ){
			boolean exclusive = Boolean.TRUE.toString().equals(value);
			if( this.exclusive != exclusive ){
				this.jackSynthesizer.openChannel(getJackChannelId(), exclusive);
			}
		} else if( this.midiChannel != null ){
			this.midiChannel.sendParameter(key, value);
		}
	}

	public int getJackChannelId() {
		return jackChannelId;
	}
	
	public MidiChannel getMidiChannel() {
		return midiChannel;
	}
	
	public void setMidiChannel(MidiChannel midiChannel) {
		this.midiChannel = midiChannel;
	}
	
	public JackPort getJackPort() {
		return jackPort;
	}
	
	public void setJackPort(JackPort jackPort) {
		this.jackPort = jackPort;
	}
	
	public boolean isExclusive() {
		return exclusive;
	}
	
	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}
}
