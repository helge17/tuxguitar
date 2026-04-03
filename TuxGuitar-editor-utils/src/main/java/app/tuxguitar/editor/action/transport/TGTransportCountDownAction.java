package app.tuxguitar.editor.action.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;

public class TGTransportCountDownAction extends TGActionBase {

	public static final String NAME = "action.transport.count-down";

	public TGTransportCountDownAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		MidiPlayer midiPlayer = MidiPlayer.getInstance(getContext());
		midiPlayer.getCountDown().setEnabled(!midiPlayer.getCountDown().isEnabled());
	}
}
