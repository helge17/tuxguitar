package org.herac.tuxguitar.android.menu.options;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCdRootAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCdUpAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.android.view.dialog.browser.collection.TGBrowserCollectionsDialogController;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

import android.view.Menu;

public class TGBrowserMenu {
	
	private TGContext context;
	private TGActivity activity;
	private Menu menu;
	
	private TGBrowserMenu(TGContext context) {
		this.context = context;
	}
	
	public void initialize(TGActivity activity, Menu menu) {
		this.activity = activity;
		this.menu = menu;
		this.initializeItems();
	}
	
	public TGContext getContext() {
		return context;
	}

	public TGActivity getActivity() {
		return activity;
	}

	public Menu getMenu() {
		return menu;
	}

	public void initializeItems() {
		this.getMenu().findItem(R.id.menu_browser_settings).setOnMenuItemClickListener(createOpenDialogAction(new TGBrowserCollectionsDialogController()));
		this.getMenu().findItem(R.id.menu_browser_root).setOnMenuItemClickListener(createBrowserAction(TGBrowserCdRootAction.NAME));
		this.getMenu().findItem(R.id.menu_browser_back).setOnMenuItemClickListener(createBrowserAction(TGBrowserCdUpAction.NAME));
		this.getMenu().findItem(R.id.menu_browser_refresh).setOnMenuItemClickListener(createBrowserAction(TGBrowserRefreshAction.NAME));
	}
	
	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(getContext(), actionId);
	}
	
	public TGActionProcessorListener createBrowserAction(String actionId) {
		TGBrowserSession tgBrowserSession = TGBrowserManager.getInstance(getContext()).getSession();
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(actionId);
		tgActionProcessor.setAttribute(TGBrowserSession.class.getName(), tgBrowserSession);
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createOpenDialogAction(TGDialogController controller) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.activity);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller);
		return tgActionProcessor;
	}
	
	public static TGBrowserMenu getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGBrowserMenu.class.getName(), new TGSingletonFactory<TGBrowserMenu>() {
			public TGBrowserMenu createInstance(TGContext context) {
				return new TGBrowserMenu(context);
			}
		});
	}
}
