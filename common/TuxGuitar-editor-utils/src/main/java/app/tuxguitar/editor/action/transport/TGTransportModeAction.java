package app.tuxguitar.editor.action.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.util.TGContext;

public class TGTransportModeAction extends TGActionBase {

	public static final String NAME = "action.transport.mode";

	public static final String ATTRIBUTE_PLAYER_MODE = "midiPlayerMode";

	public TGTransportModeAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		MidiPlayerMode mode = MidiPlayer.getInstance(getContext()).getMode();
		mode.copyFrom((MidiPlayerMode)context.getAttribute(ATTRIBUTE_PLAYER_MODE));
		mode.reset();
	}
}
