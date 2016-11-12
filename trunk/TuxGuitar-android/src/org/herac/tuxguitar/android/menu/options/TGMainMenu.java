package org.herac.tuxguitar.android.menu.options;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.android.action.impl.view.TGToggleTabKeyboardAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.fragment.TGFragmentController;
import org.herac.tuxguitar.android.fragment.impl.TGPreferencesFragmentController;
import org.herac.tuxguitar.android.menu.context.TGContextMenuController;
import org.herac.tuxguitar.android.menu.context.impl.TGBeatMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGCompositionMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGDurationMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGEditMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGEffectMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGMeasureMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGTrackMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGVelocityMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGViewMenu;
import org.herac.tuxguitar.android.menu.util.TGToggleStyledIconHandler;
import org.herac.tuxguitar.android.menu.util.TGToggleStyledIconHelper;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

import android.view.Menu;

public class TGMainMenu {
	
	private TGContext context;
	private TGActivity activity;
	private Menu menu;
	private TGToggleStyledIconHelper styledIconHelper;
	
	private TGMainMenu(TGContext context) {
		this.context = context;
		this.styledIconHelper = new TGToggleStyledIconHelper(context);
		this.fillStyledIconHandlers();
	}
	
	public void initialize(TGActivity activity, Menu menu) {
		this.activity = activity;
		this.menu = menu;
		this.initializeItems();
		this.styledIconHelper.initialize(activity, menu);
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
		this.getMenu().findItem(R.id.menu_tab_keyboard_toggle).setOnMenuItemClickListener(createActionProcessor(TGToggleTabKeyboardAction.NAME));
		this.getMenu().findItem(R.id.menu_transport_play).setOnMenuItemClickListener(createActionProcessor(TGTransportPlayAction.NAME));
		this.getMenu().findItem(R.id.menu_edit).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGEditMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_view).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGViewMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_composition).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGCompositionMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_track).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGTrackMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_measure).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGMeasureMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_beat).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGBeatMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_duration).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGDurationMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_effect).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGEffectMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_velocity).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGVelocityMenu(getActivity())));
		this.getMenu().findItem(R.id.menu_settings).setOnMenuItemClickListener(createFragmentActionProcessor(new TGPreferencesFragmentController()));
	}
	
	public void fillStyledIconHandlers() {
		this.styledIconHelper.addHandler(this.createStyledIconTransportHandler());
	}
	
	public TGToggleStyledIconHandler createStyledIconTransportHandler() {
		return new TGToggleStyledIconHandler() {
			
			public Integer getMenuItemId() {
				return R.id.menu_transport_play;
			}
			
			public Integer resolveStyle() {
				boolean running = MidiPlayer.getInstance(getContext()).isRunning();
				return (running ? R.style.mainImageButtonStopStyle : R.style.mainImageButtonPlayStyle);
			}
		};
	}
	
	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(getContext(), actionId);
	}
	
	public TGActionProcessorListener createFragmentActionProcessor(TGFragmentController<?> controller) {
		TGActionProcessorListener tgActionProcessor = new TGActionProcessorListener(getContext(), TGOpenFragmentAction.NAME);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_CONTROLLER, controller);
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
