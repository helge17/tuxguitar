package app.tuxguitar.jack.sequencer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.jack.JackClient;
import app.tuxguitar.jack.provider.JackClientProvider;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiSequencer;
import app.tuxguitar.player.base.MidiSequencerProvider;
import app.tuxguitar.util.TGContext;

public class JackSequencerProvider implements MidiSequencerProvider{

	private TGContext context;
	private List<MidiSequencer> jackSequencers;
	private JackClientProvider jackClientProvider;

	public JackSequencerProvider(TGContext context, JackClientProvider jackClientProvider){
		this.context = context;
		this.jackClientProvider = jackClientProvider;
		this.jackSequencers = new ArrayList<MidiSequencer>();
	}

	public List<MidiSequencer> listSequencers() throws MidiPlayerException {
		if( this.jackSequencers.isEmpty() ){
			JackClient jackClient = this.jackClientProvider.getJackClient();
			if( jackClient != null ){
				this.jackSequencers.add(new JackSequencer(this.context, jackClient));
			}
		}
		return this.jackSequencers;
	}

	public void clearSequencers() throws MidiPlayerException {
		this.jackSequencers.clear();
	}

	public void closeAll() throws MidiPlayerException {
		Iterator<MidiSequencer> it = listSequencers().iterator();
		while(it.hasNext()){
			MidiSequencer sequencer = (MidiSequencer)it.next();
			sequencer.close();
		}
		clearSequencers();
	}
}
