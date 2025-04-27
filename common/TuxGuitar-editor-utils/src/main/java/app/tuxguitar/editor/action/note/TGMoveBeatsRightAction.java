package app.tuxguitar.editor.action.note;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

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
