package org.herac.tuxguitar.app.action.impl.caret;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
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
		
		TablatureEditor.getInstance(getContext()).getTablature().getCaret().moveTo(track, measure, beat, string.getNumber());
	}
}
