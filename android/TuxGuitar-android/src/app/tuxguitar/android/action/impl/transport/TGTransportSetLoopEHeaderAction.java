package app.tuxguitar.android.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.transport.TGTransport;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;

public class TGTransportSetLoopEHeaderAction extends TGActionBase {

	public static final String NAME = "action.transport.set-loop-end";

	public TGTransportSetLoopEHeaderAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		MidiPlayerMode pm = MidiPlayer.getInstance(getContext()).getMode();
		int caretNb =  TGSongViewController.getInstance(getContext()).getCaret().getMeasure().getNumber();
		int measureNb = pm.getLoopEHeader() != caretNb ? caretNb : -1;
		pm.setLoopEHeader(measureNb);

		// if loop start would now be behind loop end, set it to loop end
		if (pm.getLoopSHeader() != -1 && pm.getLoopEHeader() != -1 && pm.getLoopSHeader() > measureNb) {
			pm.setLoopSHeader(measureNb);
		}
	}
}
