package app.tuxguitar.app.action.impl.edit;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

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
