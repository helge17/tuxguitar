package app.tuxguitar.android.menu.controller.impl.fragment;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.action.TGActionProcessorListener;
import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import app.tuxguitar.android.action.impl.transport.TGTransportPlayAction;
import app.tuxguitar.android.action.impl.view.TGToggleTabKeyboardAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.activity.TGActivityController;
import app.tuxguitar.android.fragment.TGFragmentController;
import app.tuxguitar.android.fragment.impl.TGPreferencesFragmentController;
import app.tuxguitar.android.menu.controller.TGMenuController;
import app.tuxguitar.android.menu.controller.impl.contextual.TGBeatMenu;
import app.tuxguitar.android.menu.controller.impl.contextual.TGCompositionMenu;
import app.tuxguitar.android.menu.controller.impl.contextual.TGDurationMenu;
import app.tuxguitar.android.menu.controller.impl.contextual.TGEditMenu;
import app.tuxguitar.android.menu.controller.impl.contextual.TGEffectMenu;
import app.tuxguitar.android.menu.controller.impl.contextual.TGMeasureMenu;
import app.tuxguitar.android.menu.controller.impl.contextual.TGTrackMenu;
import app.tuxguitar.android.menu.controller.impl.contextual.TGTransportMenu;
import app.tuxguitar.android.menu.controller.impl.contextual.TGVelocityMenu;
import app.tuxguitar.android.menu.controller.impl.contextual.TGViewMenu;
import app.tuxguitar.android.menu.util.TGToggleStyledIconHandler;
import app.tuxguitar.android.menu.util.TGToggleStyledIconHelper;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

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
		menu.findItem(R.id.action_menu_dynamic).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGVelocityMenu(getActivity())));
		menu.findItem(R.id.action_menu_effects).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGEffectMenu(getActivity())));
		menu.findItem(R.id.action_menu_transport).setOnMenuItemClickListener(createContextMenuActionProcessor(new TGTransportMenu(getActivity())));
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
