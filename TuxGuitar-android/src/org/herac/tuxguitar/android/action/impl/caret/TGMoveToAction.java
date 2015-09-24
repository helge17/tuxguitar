package org.herac.tuxguitar.android.action.impl.caret;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.util.TGContext;

public class TGMoveToAction extends TGActionBase{
	
	public static final String NAME = "action.caret.move-to";
	
	public TGMoveToAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGTrackImpl track = ((TGTrackImpl) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		TGMeasureImpl measure = ((TGMeasureImpl) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		TGString string = ((TGString) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING));
		
		getEditor().getCaret().moveTo(track, measure, beat, string.getNumber());
	}
}
