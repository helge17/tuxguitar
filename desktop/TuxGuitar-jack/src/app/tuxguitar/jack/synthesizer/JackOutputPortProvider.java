package app.tuxguitar.jack.synthesizer;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.jack.JackClient;
import app.tuxguitar.jack.provider.JackClientProvider;
import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.util.TGContext;

public class JackOutputPortProvider implements MidiOutputPortProvider{

	private TGContext context;
	private List<MidiOutputPort> jackOutputPorts;
	private JackClientProvider jackClientProvider;

	public JackOutputPortProvider(TGContext context, JackClientProvider jackClientProvider){
		this.context = context;
		this.jackClientProvider = jackClientProvider;
	}

	public List<MidiOutputPort> listPorts() {
		if( this.jackOutputPorts == null ){
			this.jackOutputPorts = new ArrayList<MidiOutputPort>();

			JackClient jackClient = this.jackClientProvider.getJackClient();
			if( jackClient != null ){
				this.jackOutputPorts.add(new JackSynthesizerPort(this.context, jackClient));
			}
		}
		return this.jackOutputPorts;
	}

	public void closeAll(){
		// TODO
	}
}
