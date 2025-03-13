package org.herac.tuxguitar.app.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.util.TGContext;

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