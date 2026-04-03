package app.tuxguitar.editor.action.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;

public class TGTransportMetronomeAction extends TGActionBase {

	public static final String NAME = "action.transport.metronome";

	public TGTransportMetronomeAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		MidiPlayer midiPlayer = MidiPlayer.getInstance(getContext());
		midiPlayer.setMetronomeEnabled(!midiPlayer.isMetronomeEnabled());
	}
}
