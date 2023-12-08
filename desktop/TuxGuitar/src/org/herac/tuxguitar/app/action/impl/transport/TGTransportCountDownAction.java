package org.herac.tuxguitar.app.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;

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
