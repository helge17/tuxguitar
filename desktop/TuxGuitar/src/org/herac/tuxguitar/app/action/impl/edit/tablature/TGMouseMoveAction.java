package org.herac.tuxguitar.app.action.impl.edit.tablature;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGMouseMoveAction extends TGActionBase{
	
	public static final String NAME = "action.edit.tablature.mouse-move";
	
	public TGMouseMoveAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		EditorKit editorKit = TablatureEditor.getInstance(getContext()).getTablature().getEditorKit();
		if( editorKit .updateSelectedMeasure(context) ) {
			context.setAttribute(ATTRIBUTE_SUCCESS, true);
		}
	}
}
