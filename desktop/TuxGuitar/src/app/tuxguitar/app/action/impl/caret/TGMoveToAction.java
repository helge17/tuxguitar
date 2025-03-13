package app.tuxguitar.app.action.impl.caret;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
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

		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		tablature.getCaret().moveTo(track, measure, beat, string.getNumber());
		if (!Boolean.TRUE.equals(context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION))) {
			tablature.getSelector().clearSelection();
		}
	}
}
