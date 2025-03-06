package org.herac.tuxguitar.app.action.impl.edit;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.dialog.errors.TGMeasureErrorDialogController;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

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
