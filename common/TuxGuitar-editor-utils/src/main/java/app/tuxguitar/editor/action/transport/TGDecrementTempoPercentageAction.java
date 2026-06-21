package app.tuxguitar.editor.action.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.util.TGContext;

public class TGDecrementTempoPercentageAction extends TGActionBase {

	public static final String NAME = "action.transport.decrement-tempo-percentage";
	
	public TGDecrementTempoPercentageAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected void processAction(TGActionContext context) {
		MidiPlayerMode mode = MidiPlayer.getInstance(getContext()).getMode();
		int decreasedTempo;
		int decrement = 1;
		
		if (mode.getType() == MidiPlayerMode.TYPE_CUSTOM) {
			decrement = mode.getCustomPercentIncrement();
		}
		
		if (mode.getCurrentPercent() > decrement) {
			decreasedTempo = mode.getCurrentPercent() - decrement;
		} else {
			decreasedTempo = MidiPlayerMode.DEFAULT_MIN_PERCENTAGE;
		}
		
		context.setAttribute(TGChangeTempoPercentageAction.ATTRIBUTE_PERCENTAGE_VALUE, decreasedTempo);
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGChangeTempoPercentageAction.NAME, context);
	}
}