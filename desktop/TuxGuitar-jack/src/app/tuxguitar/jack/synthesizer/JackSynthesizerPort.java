package app.tuxguitar.jack.synthesizer;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.jack.JackClient;
import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiSynthesizer;
import app.tuxguitar.util.TGContext;

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
				throw new MidiPlayerException(TuxGuitar.getProperty("jack.error.not-running"));
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
