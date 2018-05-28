package org.herac.tuxguitar.android.menu.controller.impl.fragment;

import android.view.Menu;
import android.view.MenuInflater;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.android.action.impl.view.TGToggleTabKeyboardAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.fragment.TGFragmentController;
import org.herac.tuxguitar.android.fragment.impl.TGPreferencesFragmentController;
import org.herac.tuxguitar.android.menu.controller.TGMenuController;
import org.herac.tuxguitar.android.menu.controller.impl.contextual.TGBeatMenu;
import org.herac.tuxguitar.android.menu.controller.impl.contextual.TGCompositionMenu;
import org.herac.tuxguitar.android.menu.controller.impl.contextual.TGDurationMenu;
import org.herac.tuxguitar.android.menu.controller.impl.contextual.TGEditMenu;
import org.herac.tuxguitar.android.menu.controller.impl.contextual.TGEffectMenu;
import org.herac.tuxguitar.android.menu.controller.impl.contextual.TGMeasureMenu;
import org.herac.tuxguitar.android.menu.controller.impl.contextual.TGTrackMenu;
import org.herac.tuxguitar.android.menu.controller.impl.contextual.TGVelocityMenu;
import org.herac.tuxguitar.android.menu.controller.impl.contextual.TGViewMenu;
import org.herac.tuxguitar.android.menu.util.TGToggleStyledIconHandler;
import org.herac.tuxguitar.android.menu.util.TGToggleStyledIconHelper;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMainMenu implements TGMenuController {
	
	private TGContext context;
	private TGToggleStyledIconHelper styledIconHelper;
	
	private TGMainMenu(TGContext context) {
		this.context = context;
		this.styledIconHelper = new TGToggleStyledIconHelper(context);
		this.fillStyledIconHandlers();
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_main, menu);
		this.initializeItems(menu);
		this.styledIconHelper.initialize(this.getActivity(), menu);
	}

	public void initializeItems(Menu menu) {
		menu.findItem(R.id.action_tab_keyboard_toggle).setOnMenuItemClickListener(createActionProcessor(TGToggleTabKeyboardAction.NAME));
		menu.findItem(R.id.action_transport_play).setOnMenuItemClickListener(createActionProcessor(TGTransportPlayAction.NAME));
		menu.findItem(R.id.action_menu_edit).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGEditMenu(getActivity())));
		menu.findItem(R.id.action_menu_view).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGViewMenu(getActivity())));
		menu.findItem(R.id.action_menu_composition).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGCompositionMenu(getActivity())));
		menu.findItem(R.id.action_menu_track).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGTrackMenu(getActivity())));
		menu.findItem(R.id.action_menu_measure).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGMeasureMenu(getActivity())));
		menu.findItem(R.id.action_menu_beat).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGBeatMenu(getActivity())));
		menu.findItem(R.id.action_menu_duration).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGDurationMenu(getActivity())));
		menu.findItem(R.id.action_menu_effect).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGEffectMenu(getActivity())));
		menu.findItem(R.id.action_menu_velocity).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGVelocityMenu(getActivity())));
		menu.findItem(R.id.action_menu_settings).setOnMenuItemClickListener(createFragmentActionProcessor(new TGPreferencesFragmentController()));
	}
	
	public void fillStyledIconHandlers() {
		this.styledIconHelper.addHandler(this.createStyledIconTransportHandler());
	}
	
	public TGToggleStyledIconHandler createStyledIconTransportHandler() {
		return new TGToggleStyledIconHandler() {
			
			public Integer getMenuItemId() {
				return R.id.action_transport_play;
			}
			
			public Integer resolveStyle() {
				boolean running = MidiPlayer.getInstance(getContext()).isRunning();
				return (running ? R.style.TGImageButton_Stop : R.style.TGImageButton_Play);
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
	
	public TGActionProcessorListener createContextMenuActionProcessor(TGMenuController controller) {
		TGActionProcessorListener tgActionProcessor = new TGActionProcessorListener(getContext(), TGOpenMenuAction.NAME);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, getActivity());
		return tgActionProcessor;
	}

	public TGContext getContext() {
		return context;
	}

	public TGActivity getActivity() {
		return TGActivityController.getInstance(this.context).getActivity();
	}

	public static TGMainMenu getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMainMenu.class.getName(), new TGSingletonFactory<TGMainMenu>() {
			public TGMainMenu createInstance(TGContext context) {
				return new TGMainMenu(context);
			}
		});
	}
}
