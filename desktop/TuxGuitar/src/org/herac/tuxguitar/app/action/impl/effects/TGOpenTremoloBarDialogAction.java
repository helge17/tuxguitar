package org.herac.tuxguitar.app.action.impl.effects;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.dialog.tremolobar.TGTremoloBarDialogController;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGOpenTremoloBarDialogAction extends TGActionBase{
	
	public static final String NAME = "action.gui.open-tremolo-bar-dialog";
	
	public TGOpenTremoloBarDialogAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext){
		tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGTremoloBarDialogController());
		TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
	}
}
