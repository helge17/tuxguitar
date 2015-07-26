package org.herac.tuxguitar.app.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

public class TGTransportSetLoopEHeaderAction extends TGActionBase {
	
	public static final String NAME = "action.transport.set-loop-end";
	
	public TGTransportSetLoopEHeaderAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		if( measure != null ){
			MidiPlayer midiPlayer = MidiPlayer.getInstance(getContext());
			MidiPlayerMode pm = midiPlayer.getMode();
			if( pm.isLoop() ){
				pm.setLoopEHeader( pm.getLoopEHeader() != measure.getNumber() ? measure.getNumber() : -1 );
			}
		}
	}
}
