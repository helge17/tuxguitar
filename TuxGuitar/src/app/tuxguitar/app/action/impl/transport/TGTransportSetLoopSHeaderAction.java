package app.tuxguitar.app.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.document.TGDocument;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
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
		TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		if( measure != null ){
			MidiPlayer midiPlayer = MidiPlayer.getInstance(getContext());
			MidiPlayerMode pm = midiPlayer.getMode();
			if( pm.isLoop() ){
				int measureNb = pm.getLoopSHeader() != measure.getNumber() ? measure.getNumber() : -1;
				pm.setLoopSHeader( measureNb );

				TGDocument document = TGDocumentListManager.getInstance(getContext()).findCurrentDocument();
				if ((document != null) && (document.getMidiPlayerMode() != null)){
					document.getMidiPlayerMode().setLoopSHeader(measureNb);
				}

				// if loop end would now be before loop start, set it to loop start
				if (pm.getLoopEHeader() != -1 && pm.getLoopSHeader() != -1  && pm.getLoopEHeader() < measureNb) {
					pm.setLoopEHeader(measureNb);
					if ((document != null) && (document.getMidiPlayerMode() != null)) {
						document.getMidiPlayerMode().setLoopEHeader(measureNb);
					}
				}
			}
		}
	}
}
