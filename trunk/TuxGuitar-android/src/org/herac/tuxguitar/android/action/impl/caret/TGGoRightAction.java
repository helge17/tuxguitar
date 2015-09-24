package org.herac.tuxguitar.android.action.impl.caret;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.view.tablature.TGCaret;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureAction;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

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
