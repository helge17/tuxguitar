package app.tuxguitar.app.action.impl.marker;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.view.TGToggleViewAction;
import app.tuxguitar.app.view.dialog.marker.TGMarkerListController;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGToggleMarkerListAction extends TGActionBase{

	public static final String NAME = "action.gui.toggle-marker-list";

	public TGToggleMarkerListAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext){
		tgActionContext.setAttribute(TGToggleViewAction.ATTRIBUTE_CONTROLLER, new TGMarkerListController());
		TGActionManager.getInstance(getContext()).execute(TGToggleViewAction.NAME, tgActionContext);
	}
}
