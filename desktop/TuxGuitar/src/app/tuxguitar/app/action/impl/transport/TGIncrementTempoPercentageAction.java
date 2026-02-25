package app.tuxguitar.app.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.TGActionProcessor;
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
		
		if (mode.getCurrentPercent() < 100) {
			increasedTempo = mode.getCurrentPercent() + 1;
		} else {
			increasedTempo = 100;
		}
		
		TGActionProcessor tgActionProcessor = new TGActionProcessor(getContext(), TGChangeTempoPercentageAction.NAME);
		tgActionProcessor.setAttribute(
			TGChangeTempoPercentageAction.ATTRIBUTE_PERCENTAGE_VALUE, 
			increasedTempo
		);
		tgActionProcessor.processOnCurrentThread();
	}
	
}