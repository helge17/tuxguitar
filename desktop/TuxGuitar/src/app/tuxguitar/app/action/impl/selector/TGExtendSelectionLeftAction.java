package app.tuxguitar.app.action.impl.selector;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.Selector;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGExtendSelectionLeftAction extends TGActionBase {

	public static final String NAME = "action.selection.extend-left";

	public TGExtendSelectionLeftAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
		Selector selector = tablature.getSelector();
		Caret caret = tablature.getCaret();
		if (!selector.isActive()) {
		    selector.initializeSelection(caret.getSelectedBeat());
		}
		caret.moveLeft();
		selector.updateSelection(caret.getSelectedBeat());
	}
}
