package app.tuxguitar.app.action.impl.view;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.view.dialog.toolbar.TGToolBarDialogController;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGOpenMainToolBarSettingsDialogAction extends TGActionBase {

	public static final String NAME = "action.gui.open-maintoolbar-settings-dialog";

	public TGOpenMainToolBarSettingsDialogAction(TGContext context) {
		super(context, NAME);
	}

	@Override
	protected void processAction(TGActionContext tgActionContext) {
		tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGToolBarDialogController());
		TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
	}

}
