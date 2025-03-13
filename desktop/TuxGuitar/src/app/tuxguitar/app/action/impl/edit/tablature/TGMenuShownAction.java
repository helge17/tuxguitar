package app.tuxguitar.app.action.impl.edit.tablature;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.caret.TGMoveToAction;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.tab.edit.EditorKit;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGMenuShownAction extends TGActionBase{

	public static final String NAME = "action.edit.tablature.menu-shown";

	public TGMenuShownAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		EditorKit editorKit = TablatureEditor.getInstance(getContext()).getTablature().getEditorKit();
		if( editorKit.fillSelection(context)) {
			TGActionManager.getInstance(getContext()).execute(TGMoveToAction.NAME, context);
		}
	}
}
