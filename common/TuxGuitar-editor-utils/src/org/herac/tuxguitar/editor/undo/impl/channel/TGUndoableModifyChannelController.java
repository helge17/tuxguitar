package org.herac.tuxguitar.editor.undo.impl.channel;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.TGUndoableEdit;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableModifyChannelController implements TGUndoableActionController {

	public TGUndoableEdit startUndoable(TGContext context, TGActionContext actionContext) {
		return TGUndoableModifyChannel.startUndo(context, ((TGChannel) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL)).getChannelId());
	}

	public TGUndoableEdit endUndoable(TGContext context, TGActionContext actionContext, TGUndoableEdit undoableEdit) {
		return ((TGUndoableModifyChannel) undoableEdit).endUndo();
	}
}
