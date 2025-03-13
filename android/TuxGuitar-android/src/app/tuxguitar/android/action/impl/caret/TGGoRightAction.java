package app.tuxguitar.android.action.impl.caret;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGGoRightAction extends TGActionBase{

	public static final String NAME = "action.caret.go-right";

	public TGGoRightAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGCaret caret = getEditor().getCaret();
		if(!caret.moveRight()){
			context.setAttribute(TGAddMeasureAction.ATTRIBUTE_MEASURE_NUMBER, Integer.valueOf((song.countMeasureHeaders() + 1)));

			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGAddMeasureAction.NAME, context);

			caret.moveRight();
		}
	}
}
