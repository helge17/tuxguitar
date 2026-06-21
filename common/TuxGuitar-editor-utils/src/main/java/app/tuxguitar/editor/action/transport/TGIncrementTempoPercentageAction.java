package app.tuxguitar.editor.action.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.util.TGContext;

public class TGIncrementTempoPercentageAction extends TGActionBase {

	public static final String NAME = "action.transport.increment-tempo-percentage";

	public TGIncrementTempoPercentageAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected void processAction(TGActionContext context) {
		MidiPlayerMode mode = MidiPlayer.getInstance(getContext()).getMode();
		int increasedTempo;
		int increment = 1;
		
		if (mode.getType() == MidiPlayerMode.TYPE_CUSTOM) {
			increment = mode.getCustomPercentIncrement();
		}
		
		if (mode.getCurrentPercent() <= (MidiPlayerMode.DEFAULT_MAX_PERCENTAGE - increment)) {
			increasedTempo = mode.getCurrentPercent() + increment;
		} else {
			increasedTempo = MidiPlayerMode.DEFAULT_MAX_PERCENTAGE;
		}
		
		context.setAttribute(TGChangeTempoPercentageAction.ATTRIBUTE_PERCENTAGE_VALUE, increasedTempo);
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGChangeTempoPercentageAction.NAME, context);
	}
	
}