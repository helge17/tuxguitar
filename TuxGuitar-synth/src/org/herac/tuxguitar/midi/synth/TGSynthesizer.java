package org.herac.tuxguitar.midi.synth;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.player.base.MidiChannel;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiSynthesizer;
import org.herac.tuxguitar.util.TGContext;

public class TGSynthesizer implements MidiSynthesizer{
	
	public static final int BANKS_LENGTH = 129;
	public static final int PROGRAMS_LENGTH = 128;
	
	private TGContext context;
	
	private List<TGSynthChannel> channels;
	private TGProgram[][] programs;
	private TGSynthThread synthesizerThread;
	
	public TGSynthesizer(TGContext context){
		this.context = context;
		this.synthesizerThread = new TGSynthThread( this );
		this.channels = new ArrayList<TGSynthChannel>();
		this.programs = new TGProgram[BANKS_LENGTH][PROGRAMS_LENGTH];
		for( int b = 0; b < BANKS_LENGTH ; b ++ ){
			for( int p = 0; p < PROGRAMS_LENGTH ; p ++ ) {
				this.programs[ b ][ p ] = new TGProgram();
			}
		}
	}
	
	public boolean isOpen(){
		return this.synthesizerThread.isRunning();
	}
	
	public void open() throws MidiPlayerException{
		this.synthesizerThread.start();
	}
	
	public void close() throws MidiPlayerException{
		if( this.isOpen() ){
			this.synthesizerThread.stop();
			this.closeChannels();
		}
	}
	
	public MidiChannel openChannel(int channelId) throws MidiPlayerException{
		TGSynthChannel vstChannel = new TGSynthChannel(this, channelId);
		
		this.channels.add(vstChannel);
		
		return vstChannel;
	}
	
	public void closeChannel(MidiChannel midiChannel) throws MidiPlayerException{
		TGSynthChannel tgChannel = ((TGSynthChannel)midiChannel);
		tgChannel.closeProcessor();
		
		this.channels.remove(tgChannel);
	}
	
	public boolean isChannelOpen(MidiChannel midiChannel) throws MidiPlayerException{
		return true;
	}
	
	public void closeChannels() throws MidiPlayerException{
		while( countChannels() > 0 ){
			closeChannel( getChannel(0) );
		}
	}
	
	public TGSynthChannel getChannel( int index ){
		if( index >= 0 && index < countChannels() ){
			return (TGSynthChannel)this.channels.get(index);
		}
		return null;
	}
	
	public TGSynthChannel getChannelById( int channelId ){
		for(TGSynthChannel channel : this.channels) {
			if( channel.getId() == channelId ) {
				return channel;
			}
		}
		return null;
	}
	
	public int countChannels(){
		return this.channels.size();
	}
	
	public TGProgram getProgram(int bank, int program){
		if( bank >= 0 && bank < this.programs.length ){
			if( program >= 0 && program < this.programs[bank].length ){
				return this.programs[bank][program];
			}
		}
		return null;
	}

	public TGContext getContext() {
		return context;
	}
}
