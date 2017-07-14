package org.herac.tuxguitar.android.action.impl.gui;

import android.view.View;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.context.TGMenuCabCallBack;
import org.herac.tuxguitar.android.menu.context.TGMenuController;
import org.herac.tuxguitar.util.TGContext;

public class TGOpenCabMenuAction extends TGActionBase{

	public static final String NAME = "action.gui.open-cab-menu";

	public static final String ATTRIBUTE_MENU_ACTIVITY = TGActivity.class.getName();
	public static final String ATTRIBUTE_MENU_CONTROLLER = TGMenuController.class.getName();
	public static final String ATTRIBUTE_MENU_SELECTABLE_VIEW = "selectableView";

	public TGOpenCabMenuAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		context.setAttribute(TGMenuCabCallBack.ATTRIBUTE_BY_PASS_CLOSE_MENU, true);

		View selectableView = context.getAttribute(ATTRIBUTE_MENU_SELECTABLE_VIEW);
		TGMenuController tgMenuController = context.getAttribute(ATTRIBUTE_MENU_CONTROLLER);
		TGActivity tgActivity = context.getAttribute(ATTRIBUTE_MENU_ACTIVITY);
		tgActivity.startActionMode(new TGMenuCabCallBack(this.getContext(), tgMenuController, selectableView));
	}
}
