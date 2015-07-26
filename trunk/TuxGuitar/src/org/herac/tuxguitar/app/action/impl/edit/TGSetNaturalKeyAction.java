package org.herac.tuxguitar.app.action.impl.edit;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGSetNaturalKeyAction extends TGActionBase{
	
	public static final String NAME = "action.edit.set-natural-key";
	
	public TGSetNaturalKeyAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TablatureEditor tablatureEditor = TablatureEditor.getInstance(getContext());
		tablatureEditor.getTablature().getEditorKit().setNatural(!tablatureEditor.getTablature().getEditorKit().isNatural());
	}
}
