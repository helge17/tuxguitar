package org.herac.tuxguitar.app.action.impl.selector;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

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
