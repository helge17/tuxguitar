package app.tuxguitar.app.action.impl.caret;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGGoLineDownAction extends TGActionBase {

	public static final String NAME = "action.caret.go-line-down";

	public TGGoLineDownAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		if (tablature.getCaret().moveLineDown()
				&& !Boolean.TRUE.equals(context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION))) {
			tablature.getSelector().clearSelection();
		}
	}
}
