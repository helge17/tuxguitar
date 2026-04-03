package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

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
