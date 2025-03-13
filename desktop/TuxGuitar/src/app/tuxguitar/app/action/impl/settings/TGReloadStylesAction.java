package app.tuxguitar.app.action.impl.settings;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGReloadStylesAction extends TGActionBase {

	public static final String NAME = "action.system.reload-styles";

	public TGReloadStylesAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TablatureEditor.getInstance(getContext()).getTablature().reloadStyles();
	}
}