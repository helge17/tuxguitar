package org.herac.tuxguitar.android.menu.options;

import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.fragment.TGFragment;
import org.herac.tuxguitar.android.menu.context.TGContextMenuController;
import org.herac.tuxguitar.android.menu.context.impl.TGBeatMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGCompositionMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGEditMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGFileMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGMeasureMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGTrackMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGTransportMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGViewMenu;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

import android.view.Menu;

public class TGMainMenu {
	
	private TGContext context;
	private TGActivity activity;
	private Menu menu;
	
	private TGMainMenu(TGContext context) {
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
		this.getMenu().findItem(R.id.menu_file).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGFileMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_edit).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGEditMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_view).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGViewMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_transport).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGTransportMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_composition).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGCompositionMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_track).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGTrackMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_measure).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGMeasureMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_beat).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGBeatMenu(getActivity())));
	}
	
	public TGActionProcessorListener createFragmentActionProcessor(TGFragment fragment) {
		TGActionProcessorListener tgActionProcessor = new TGActionProcessorListener(getContext(), TGOpenFragmentAction.NAME);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_FRAGMENT, fragment);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, getActivity());
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createContextMenuActionProcessor(TGContextMenuController controller) {
		TGActionProcessorListener tgActionProcessor = new TGActionProcessorListener(getContext(), TGOpenMenuAction.NAME);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, getActivity());
		return tgActionProcessor;
	}
	
	public static TGMainMenu getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMainMenu.class.getName(), new TGSingletonFactory<TGMainMenu>() {
			public TGMainMenu createInstance(TGContext context) {
				return new TGMainMenu(context);
			}
		});
	}
}
