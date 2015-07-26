package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeStrokeAction extends TGActionBase {
	
	public static final String NAME = "action.beat.general.change-stroke";
	
	public static final String ATTRIBUTE_STROKE_DIRECTION = "strokeDirection";
	public static final String ATTRIBUTE_STROKE_VALUE = "strokeValue";
	
	public TGChangeStrokeAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		int strokeDirection = ((Integer) context.getAttribute(ATTRIBUTE_STROKE_DIRECTION)).intValue();
		int strokeValue = ((Integer) context.getAttribute(ATTRIBUTE_STROKE_VALUE)).intValue();
		
		if( getSongManager(context).getMeasureManager().setStroke(measure, beat.getStart(), strokeValue, strokeDirection) ) {
			context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
		}
	}
}
