package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;

public class TGChangePickStrokeUpAction extends TGActionBase {

	public static final String NAME = "action.beat.general.change-pick-stroke-up";

	public TGChangePickStrokeUpAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		if( getSongManager(context).getMeasureManager().changePickStrokeUp(measure, beat.getStart()) ) {
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}

}
