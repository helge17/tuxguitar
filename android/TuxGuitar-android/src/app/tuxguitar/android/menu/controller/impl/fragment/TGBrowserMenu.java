package app.tuxguitar.android.menu.controller.impl.fragment;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.action.TGActionProcessorListener;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdRootAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdUpAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction;
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.activity.TGActivityController;
import app.tuxguitar.android.browser.TGBrowserManager;
import app.tuxguitar.android.browser.model.TGBrowserSession;
import app.tuxguitar.android.menu.controller.TGMenuController;
import app.tuxguitar.android.view.dialog.TGDialogController;
import app.tuxguitar.android.view.dialog.browser.collection.TGBrowserCollectionsDialogController;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGBrowserMenu implements TGMenuController {

	private TGContext context;

	private TGBrowserMenu(TGContext context) {
		this.context = context;
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_browser, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		menu.findItem(R.id.action_browser_settings).setOnMenuItemClickListener(createOpenDialogAction(new TGBrowserCollectionsDialogController()));
		menu.findItem(R.id.action_browser_root).setOnMenuItemClickListener(createBrowserAction(TGBrowserCdRootAction.NAME));
		menu.findItem(R.id.action_browser_back).setOnMenuItemClickListener(createBrowserAction(TGBrowserCdUpAction.NAME));
		menu.findItem(R.id.action_browser_refresh).setOnMenuItemClickListener(createBrowserAction(TGBrowserRefreshAction.NAME));
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
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.getActivity());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller);
		return tgActionProcessor;
	}

	public TGContext getContext() {
		return context;
	}

	public TGActivity getActivity() {
		return TGActivityController.getInstance(this.context).getActivity();
	}

	public static TGBrowserMenu getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGBrowserMenu.class.getName(), new TGSingletonFactory<TGBrowserMenu>() {
			public TGBrowserMenu createInstance(TGContext context) {
				return new TGBrowserMenu(context);
			}
		});
	}
}
