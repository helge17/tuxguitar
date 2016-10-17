package org.herac.tuxguitar.android.menu.context.impl;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.context.TGContextMenuBase;
import org.herac.tuxguitar.android.view.dialog.track.TGTrackChannelDialogController;
import org.herac.tuxguitar.android.view.dialog.track.TGTrackNameDialogController;
import org.herac.tuxguitar.android.view.dialog.track.TGTrackTuningDialogController;
import org.herac.tuxguitar.android.view.tablature.TGCaret;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.editor.action.track.TGAddNewTrackAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import org.herac.tuxguitar.editor.action.track.TGCloneTrackAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import org.herac.tuxguitar.editor.action.track.TGRemoveTrackAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

import android.view.ContextMenu;
import android.view.MenuInflater;

public class TGTrackMenu extends TGContextMenuBase {
	
	public TGTrackMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(ContextMenu menu, MenuInflater inflater) {
		menu.setHeaderTitle(R.string.menu_track);
		inflater.inflate(R.menu.menu_track, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(ContextMenu menu) {
		TGContext context = findContext();
		TGCaret caret = TGSongViewController.getInstance(context).getCaret();
		TGTrack track = caret.getTrack();
		boolean running = MidiPlayer.getInstance(context).isRunning();
		
		this.initializeItem(menu, R.id.menu_track_add, this.createActionProcessor(TGAddNewTrackAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_track_remove, this.createActionProcessor(TGRemoveTrackAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_track_clone, this.createActionProcessor(TGCloneTrackAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_track_move_up, this.createActionProcessor(TGMoveTrackUpAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_track_move_down, this.createActionProcessor(TGMoveTrackDownAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_track_change_solo, this.createActionProcessor(TGChangeTrackSoloAction.NAME), !running, track.isSolo());
		this.initializeItem(menu, R.id.menu_track_change_mute, this.createActionProcessor(TGChangeTrackMuteAction.NAME), !running, track.isMute());
		this.initializeItem(menu, R.id.menu_track_set_name, new TGTrackNameDialogController(), !running);
		this.initializeItem(menu, R.id.menu_track_set_channel, new TGTrackChannelDialogController(), !running);
		this.initializeItem(menu, R.id.menu_track_change_tuning, new TGTrackTuningDialogController(), !running);
	}
}