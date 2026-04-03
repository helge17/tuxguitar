package app.tuxguitar.app.action.impl.selector;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.Selector;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGClearSelectionAction extends TGActionBase {

	public static final String NAME = "action.selection.clear";

	public TGClearSelectionAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		Selector selector = TablatureEditor.getInstance(getContext()).getTablature().getSelector();
		selector.clearSelection();
	}
}
