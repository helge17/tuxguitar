package org.herac.tuxguitar.app.action.impl.tools;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.dialog.transpose.TGTransposeDialogController;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGOpenTransposeDialogAction extends TGActionBase{
	
	public static final String NAME = "action.gui.open-transpose-dialog";
	
	public TGOpenTransposeDialogAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext){
		tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGTransposeDialogController());
		TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
	}
}
