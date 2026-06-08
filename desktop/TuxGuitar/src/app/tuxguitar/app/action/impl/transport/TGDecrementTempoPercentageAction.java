package app.tuxguitar.app.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.TGActionProcessor;
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
		
		if (mode.getCurrentPercent() > 1) {
			decreasedTempo = mode.getCurrentPercent() - 1;
		} else {
			decreasedTempo = 1;
		}
		
		TGActionProcessor tgActionProcessor = new TGActionProcessor(getContext(), TGChangeTempoPercentageAction.NAME);
		tgActionProcessor.setAttribute(
			TGChangeTempoPercentageAction.ATTRIBUTE_PERCENTAGE_VALUE, 
			decreasedTempo
		);
		tgActionProcessor.processOnCurrentThread();
	}
	
}