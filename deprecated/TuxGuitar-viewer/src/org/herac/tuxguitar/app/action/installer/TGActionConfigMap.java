package org.herac.tuxguitar.app.action.installer;

import org.herac.tuxguitar.app.action.TGActionMap;
import org.herac.tuxguitar.app.action.impl.layout.SetChordDiagramEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.SetChordNameEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.SetScoreEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.SetTablatureEnabledAction;
import org.herac.tuxguitar.app.action.impl.measure.GoNextMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.GoPreviousMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.SelectMeasureAction;
import org.herac.tuxguitar.app.action.impl.settings.SettingsAction;
import org.herac.tuxguitar.app.action.impl.track.SelectTrackAction;
import org.herac.tuxguitar.app.action.impl.transport.TransportMixerAction;
import org.herac.tuxguitar.app.action.impl.transport.TransportPlayAction;
import org.herac.tuxguitar.app.action.impl.transport.TransportSetupAction;
import org.herac.tuxguitar.app.action.impl.transport.TransportStopAction;
import org.herac.tuxguitar.app.action.listener.cache.TGUpdateController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateItemsController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateSongController;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;

public class TGActionConfigMap extends TGActionMap<TGActionConfig> {
	
	public static final int LOCKABLE = 0x01;
	public static final int SYNC_THREAD = 0x02;
	public static final int SHORTCUT = 0x04;
	public static final int DISABLE_ON_PLAY = 0x08;
	public static final int STOP_TRANSPORT = 0x10;
	public static final int SAVE_BEFORE = 0x20;
	
	private static final TGUpdateController UPDATE_ITEMS_CTL = new TGUpdateItemsController();
	private static final TGUpdateController UPDATE_SONG_CTL = new TGUpdateSongController();
	
	public TGActionConfigMap() {
		this.createConfigMap();
	}
	
	public void createConfigMap() {
		//layout actions
		this.map(SetScoreEnabledAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
		this.map(SetTablatureEnabledAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
		this.map(SetChordNameEnabledAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
		this.map(SetChordDiagramEnabledAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
		
		//track actions	
		this.map(SelectTrackAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_ITEMS_CTL);
		
		//measure actions
		this.map(SelectMeasureAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(GoNextMeasureAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
		this.map(GoPreviousMeasureAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
		
		//player actions
		this.map(TransportPlayAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TransportStopAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TransportMixerAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TransportSetupAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		
		//Settings
		this.map(SettingsAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
	}
	
	private void map(String actionId, int flags, TGUpdateController updateController) {
		this.map(actionId, flags, updateController, null);
	}
	
	private void map(String actionId, int flags, TGUpdateController updateController, TGUndoableActionController undoableController) {
		TGActionConfig tgActionConfig = new TGActionConfig();
		tgActionConfig.setUpdateController(updateController);
		tgActionConfig.setLockableAction((flags & LOCKABLE) != 0);
		tgActionConfig.setShortcutAvailable((flags & SHORTCUT) != 0);
		tgActionConfig.setDisableOnPlaying((flags & DISABLE_ON_PLAY) != 0);
		tgActionConfig.setSyncThread((flags & SYNC_THREAD) != 0);
		
		this.set(actionId, tgActionConfig);
	}
}
