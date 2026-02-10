package app.tuxguitar.app.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.util.TGContext;

public class TGChangeTempoPercentageAction extends TGActionBase {
	
	public static final String NAME = "action.transport.change-tempo-percentage";
	public static final String ATTRIBUTE_PERCENTAGE_VALUE = "tempoPercentageValue";
	
	public TGChangeTempoPercentageAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected void processAction(TGActionContext context) {
		MidiPlayer midiPlayer = MidiPlayer.getInstance(getContext());
		MidiPlayerMode mode = midiPlayer.getMode();
		int tempoPercent = (int) context.getAttribute(ATTRIBUTE_PERCENTAGE_VALUE);
		
		if (mode.getType() == MidiPlayerMode.TYPE_CUSTOM) {
			mode.setCustomPercentFrom(tempoPercent);
			
			if (tempoPercent > mode.getCustomPercentTo()) {
				mode.setCustomPercentTo(tempoPercent);
			}
		} else {
			mode.setSimplePercent(tempoPercent);
		}
		
		mode.setCurrentPercent(tempoPercent);
	}
}
