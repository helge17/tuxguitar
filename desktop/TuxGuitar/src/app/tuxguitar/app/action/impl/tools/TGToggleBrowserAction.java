package app.tuxguitar.app.action.impl.tools;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.view.TGToggleViewAction;
import app.tuxguitar.app.view.dialog.browser.main.TGBrowserDialogController;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGToggleBrowserAction extends TGActionBase{

	public static final String NAME = "action.gui.toggle-browser-dialog";

	public TGToggleBrowserAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext){
		tgActionContext.setAttribute(TGToggleViewAction.ATTRIBUTE_CONTROLLER, new TGBrowserDialogController());
		TGActionManager.getInstance(getContext()).execute(TGToggleViewAction.NAME, tgActionContext);
	}
}
