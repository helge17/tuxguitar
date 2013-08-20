package org.herac.tuxguitar.jack.sequencer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.provider.JackClientProvider;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiSequencer;
import org.herac.tuxguitar.player.base.MidiSequencerProvider;

public class JackSequencerProvider implements MidiSequencerProvider{
	
	private List jackSequencers;
	private JackClientProvider jackClientProvider;
	
	public JackSequencerProvider(JackClientProvider jackClientProvider){
		this.jackClientProvider = jackClientProvider;
		this.jackSequencers = new ArrayList();
	}
	
	public List listSequencers() throws MidiPlayerException {
		if( this.jackSequencers.isEmpty() ){
			JackClient jackClient = this.jackClientProvider.getJackClient();
			if( jackClient != null ){
				this.jackSequencers.add(new JackSequencer(jackClient));
			}
		}
		return this.jackSequencers;
	}
	
	public void clearSequencers() throws MidiPlayerException {
		this.jackSequencers.clear();
	}
	
	public void closeAll() throws MidiPlayerException {
		Iterator it = listSequencers().iterator();
		while(it.hasNext()){
			MidiSequencer sequencer = (MidiSequencer)it.next();
			sequencer.close();
		}
		clearSequencers();
	}
}
