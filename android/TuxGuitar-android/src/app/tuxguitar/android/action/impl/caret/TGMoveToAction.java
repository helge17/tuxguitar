package app.tuxguitar.android.action.impl.caret;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.graphics.control.TGMeasureImpl;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.util.TGContext;

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
