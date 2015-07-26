package org.herac.tuxguitar.editor.undo;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.util.TGContext;

public interface TGUndoableActionController {
	
	TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext);
	
	TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit);
}
