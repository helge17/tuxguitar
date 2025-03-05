package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

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
