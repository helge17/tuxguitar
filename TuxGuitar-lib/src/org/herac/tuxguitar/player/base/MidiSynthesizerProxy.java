package org.herac.tuxguitar.player.base;

public class MidiSynthesizerProxy implements MidiSynthesizer{
	
	private MidiSynthesizer midiSynthesizer;
	
	public MidiSynthesizerProxy(){
		super();
	}
	
	public MidiChannel openChannel(int channelId) throws MidiPlayerException{
		if( this.midiSynthesizer != null ){
			return this.midiSynthesizer.openChannel(channelId);
		}
		return null;
	}
	
	public void closeChannel(MidiChannel midiChannel) throws MidiPlayerException{
		if( this.midiSynthesizer != null ){
			this.midiSynthesizer.closeChannel(midiChannel);
		}
	}
	
	public boolean isChannelOpen(MidiChannel midiChannel) throws MidiPlayerException{
		if( this.midiSynthesizer != null ){
			return this.midiSynthesizer.isChannelOpen(midiChannel);
		}
		return false;
	}
	
	public MidiSynthesizer getMidiSynthesizer() {
		return this.midiSynthesizer;
	}
	
	public void setMidiSynthesizer(MidiSynthesizer midiSynthesizer) {
		this.midiSynthesizer = midiSynthesizer;
	}
}
