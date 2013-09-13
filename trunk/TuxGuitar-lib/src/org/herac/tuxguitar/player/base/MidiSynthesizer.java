package org.herac.tuxguitar.player.base;

public interface MidiSynthesizer {
	
	public MidiChannel openChannel(int channelId) throws MidiPlayerException;
	
	public void closeChannel(MidiChannel midiChannel) throws MidiPlayerException;
	
	public boolean isChannelOpen(MidiChannel midiChannel) throws MidiPlayerException;
}
