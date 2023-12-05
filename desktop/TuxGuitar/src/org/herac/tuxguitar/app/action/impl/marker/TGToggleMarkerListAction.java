package org.herac.tuxguitar.app.action.impl.marker;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.view.TGToggleViewAction;
import org.herac.tuxguitar.app.view.dialog.marker.TGMarkerListController;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

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
