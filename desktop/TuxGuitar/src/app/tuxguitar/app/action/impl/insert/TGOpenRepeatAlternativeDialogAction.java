package app.tuxguitar.app.action.impl.insert;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.view.dialog.repeat.TGRepeatAlternativeDialogController;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGOpenRepeatAlternativeDialogAction extends TGActionBase{

	public static final String NAME = "action.gui.open-repeat-alternative-dialog";

	public TGOpenRepeatAlternativeDialogAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext){
		tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGRepeatAlternativeDialogController());
		TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
	}
}