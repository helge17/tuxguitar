package org.herac.tuxguitar.android.action.impl.gui;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.controller.TGMenuContextualInflater;
import org.herac.tuxguitar.android.menu.controller.TGMenuController;
import org.herac.tuxguitar.util.TGContext;

public class TGOpenMenuAction extends TGActionBase{
	
	public static final String NAME = "action.gui.open-menu";
	
	public static final String ATTRIBUTE_MENU_ACTIVITY = TGActivity.class.getName();
	public static final String ATTRIBUTE_MENU_CONTROLLER = TGMenuController.class.getName();
	
	public TGOpenMenuAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		TGMenuController tgMenuController = context.getAttribute(ATTRIBUTE_MENU_CONTROLLER);
		TGMenuContextualInflater.getInstance(this.getContext()).setController(tgMenuController);

		TGActivity tgActivity = context.getAttribute(ATTRIBUTE_MENU_ACTIVITY);
		tgActivity.openContextMenu();
	}
}
