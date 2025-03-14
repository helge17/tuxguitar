package app.tuxguitar.app.action.impl.edit;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.view.dialog.errors.TGMeasureErrorDialogController;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGOpenMeasureErrorsDialogAction extends TGActionBase{
	
	public static final String NAME = "action.edit.display-errors";

	public TGOpenMeasureErrorsDialogAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected void processAction(TGActionContext tgActionContext) {
		tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGMeasureErrorDialogController());
		TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
	}


}
