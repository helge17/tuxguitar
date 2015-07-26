package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGContext;

public class TGMoveBeatsRightAction extends TGActionBase {
	
	public static final String NAME = "action.beat.general.move-right";
	
	public TGMoveBeatsRightAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext) {
		TGVoice voice = ((TGVoice) tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE));
		if( voice != null ){
			tgActionContext.setAttribute(TGMoveBeatsAction.ATTRIBUTE_MOVE, Long.valueOf(voice.getDuration().getTime()));
			TGActionManager.getInstance(getContext()).execute(TGMoveBeatsAction.NAME, tgActionContext);
		}
	}
}
