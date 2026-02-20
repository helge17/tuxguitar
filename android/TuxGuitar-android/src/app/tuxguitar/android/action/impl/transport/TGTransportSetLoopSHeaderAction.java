package app.tuxguitar.android.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.transport.TGTransport;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;

public class TGTransportSetLoopSHeaderAction extends TGActionBase {

	public static final String NAME = "action.transport.set-loop-start";

	public TGTransportSetLoopSHeaderAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		MidiPlayerMode pm = MidiPlayer.getInstance(getContext()).getMode();
		int caretNb =  TGSongViewController.getInstance(getContext()).getCaret().getMeasure().getNumber();
		int measureNb = pm.getLoopSHeader() != caretNb ? caretNb : -1;
		pm.setLoopSHeader(measureNb);

		// if loop end would now be before loop start, set it to loop start
		if (pm.getLoopEHeader() != -1 && pm.getLoopSHeader() != -1  && pm.getLoopEHeader() < measureNb) {
			pm.setLoopEHeader(measureNb);
		}
	}
}
