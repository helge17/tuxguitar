package app.tuxguitar.android.action.impl.gui;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuContextualInflater;
import app.tuxguitar.android.menu.controller.TGMenuController;
import app.tuxguitar.util.TGContext;

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
