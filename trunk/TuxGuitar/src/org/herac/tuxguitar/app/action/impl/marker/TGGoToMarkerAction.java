package org.herac.tuxguitar.app.action.impl.marker;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

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
