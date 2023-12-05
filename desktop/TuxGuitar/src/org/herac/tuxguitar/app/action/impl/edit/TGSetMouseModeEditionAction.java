package org.herac.tuxguitar.app.action.impl.edit;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGSetMouseModeEditionAction extends TGActionBase{
	
	public static final String NAME = "action.edit.set-mouse-mode-edition";
	
	public TGSetMouseModeEditionAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TablatureEditor.getInstance(getContext()).getTablature().getEditorKit().setMouseMode(EditorKit.MOUSE_MODE_EDITION);
	}
}
