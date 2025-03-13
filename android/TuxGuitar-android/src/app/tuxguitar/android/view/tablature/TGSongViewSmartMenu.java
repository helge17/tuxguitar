package org.herac.tuxguitar.android.view.tablature;

import org.herac.tuxguitar.android.action.impl.gui.TGOpenCabMenuAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.menu.controller.TGMenuController;
import org.herac.tuxguitar.android.menu.controller.impl.smart.TGSelectedBeatMenu;
import org.herac.tuxguitar.android.menu.controller.impl.smart.TGSelectedMeasureMenu;
import org.herac.tuxguitar.android.menu.controller.impl.smart.TGSelectedNoteMenu;
import org.herac.tuxguitar.android.menu.controller.impl.smart.TGSelectedTrackMenu;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGAbstractContext;

public class TGSongViewSmartMenu {

	public static final String REQUEST_SMART_MENU = "requestSmartMenu";

	public static final String TRACK_AREA_SELECTED = "trackAreaSelected";
	public static final String MEASURE_AREA_SELECTED = "measureAreaSelected";

	private TGSongViewController controller;

	public TGSongViewSmartMenu(TGSongViewController controller) {
		this.controller = controller;
	}

	public void openCabMenuAction(TGActivity activity, TGMenuController controller) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.controller.getContext(), TGOpenCabMenuAction.NAME);
		tgActionProcessor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_ACTIVITY, activity);
		tgActionProcessor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_SELECTABLE_VIEW, null);
		tgActionProcessor.process();
	}

	public void openSmartMenu(TGAbstractContext context) {
		if(!MidiPlayer.getInstance(this.controller.getContext()).isRunning()) {
			TGActivity activity = TGActivityController.getInstance(this.controller.getContext()).getActivity();
			if (activity != null) {
				if( Boolean.TRUE.equals(context.getAttribute(TRACK_AREA_SELECTED))) {
					this.openCabMenuAction(activity, new TGSelectedTrackMenu(activity));
				} else if( Boolean.TRUE.equals(context.getAttribute(MEASURE_AREA_SELECTED))) {
					this.openCabMenuAction(activity, new TGSelectedMeasureMenu(activity));
				} else{
					TGNote note = this.controller.getCaret().getSelectedNote();
					if (note != null) {
						this.openCabMenuAction(activity, new TGSelectedNoteMenu(activity));
					} else {
						this.openCabMenuAction(activity, new TGSelectedBeatMenu(activity));
					}
				}
			}
		}
	}
}
