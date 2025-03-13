package org.herac.tuxguitar.app.action.impl.edit;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import org.herac.tuxguitar.util.TGContext;

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
