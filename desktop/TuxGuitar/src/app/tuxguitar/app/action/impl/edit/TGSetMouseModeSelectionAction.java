package app.tuxguitar.app.action.impl.edit;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.tab.edit.EditorKit;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGSetMouseModeSelectionAction extends TGActionBase{

	public static final String NAME = "action.edit.set-mouse-mode-selection";

	public TGSetMouseModeSelectionAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TablatureEditor.getInstance(getContext()).getTablature().getEditorKit().setMouseMode(EditorKit.MOUSE_MODE_SELECTION);
	}
}
