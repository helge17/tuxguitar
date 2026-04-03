package app.tuxguitar.app.action.impl.marker;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.caret.TGMoveToAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;

public class TGGoToMarkerAction extends TGActionBase{

	public static final String NAME = "action.marker.go-to";

	public TGGoToMarkerAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSongManager manager = getSongManager(context);
		TGMarker marker = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER);
		TGTrackImpl track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);

		TGMeasure measure = manager.getTrackManager().getMeasure(track,marker.getMeasure());
		if(measure != null){
			TGBeat beat = manager.getMeasureManager().getFirstBeat(measure.getBeats());
			if( beat != null ){
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);

				TGActionManager.getInstance(getContext()).execute(TGMoveToAction.NAME, context);
			}
		}
	}
}
