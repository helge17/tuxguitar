package app.tuxguitar.app.action.impl.edit;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import app.tuxguitar.util.TGContext;

public class TGCutAction extends TGActionBase{

	public static final String NAME = "action.edit.cut";

	public TGCutAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		if (Boolean.TRUE.equals(context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SELECTION_IS_ACTIVE))) {
			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGCopyAction.NAME, context);
			tgActionManager.execute(TGDeleteNoteOrRestAction.NAME, context);
		}
	}
}
