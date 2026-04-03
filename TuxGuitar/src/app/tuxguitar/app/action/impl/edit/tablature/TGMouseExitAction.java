package app.tuxguitar.app.action.impl.edit.tablature;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.tab.edit.EditorKit;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.event.TGUpdateMeasuresEvent;
import app.tuxguitar.util.TGContext;

public class TGMouseExitAction extends TGActionBase{

	public static final String NAME = "action.edit.tablature.mouse-exit";

	public TGMouseExitAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		EditorKit editorKit = TablatureEditor.getInstance(getContext()).getTablature().getEditorKit();
		editorKit .resetSelectedMeasure();
		// do not update caret position when updating measures following this action
		// else it would trigger an unexpected caret movement
		context.setAttribute(TGUpdateMeasuresEvent.PROPERTY_UPDATE_CARET, Boolean.FALSE);
	}
}
