package org.herac.tuxguitar.jack.synthesizer;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiSynthesizer;
import org.herac.tuxguitar.util.TGContext;

public class JackSynthesizerPort implements MidiOutputPort{
	
	private JackClient jackClient;
	private JackSynthesizer jackSynthesizer;
	
	public JackSynthesizerPort(TGContext context, JackClient jackClient){
		this.jackClient = jackClient;
		this.jackSynthesizer = new JackSynthesizer(context, this.jackClient);
	}
	
	public MidiSynthesizer getSynthesizer() throws MidiPlayerException{
		return this.jackSynthesizer;
	}
	
	public void open(){
		if(!this.jackClient.isOpen()){
			this.jackClient.open();
		}
	}
	
	public void close(){
		this.jackSynthesizer.closeAllChannels();
	}
	
	public void check() throws MidiPlayerException {
		if(!this.jackClient.isOpen() ){
			this.open();
			if(!this.jackClient.isOpen() ){
				throw new MidiPlayerException("Jack server not running?");
			}
		}
	}
	
	public String getKey(){
		return ("tuxguitar-jack");
	}
	
	public String getName(){
		return ("Jack Midi Port");
	}
}
