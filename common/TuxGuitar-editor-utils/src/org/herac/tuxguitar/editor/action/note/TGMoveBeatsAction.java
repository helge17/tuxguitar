package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGMoveBeatsAction extends TGActionBase {
	
	public static final String NAME = "action.beat.general.move";
	
	public static final String ATTRIBUTE_MOVE = "move";
	
	public TGMoveBeatsAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		Long theMove = context.getAttribute(ATTRIBUTE_MOVE);
		if( beat != null && measure != null && track != null && theMove != null ){
			getSongManager(context).getTrackManager().moveTrackBeats(track, measure.getStart(), beat.getStart(), theMove);
		}
	}
}
