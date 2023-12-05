package org.herac.tuxguitar.android.menu.controller.impl.contextual;

import android.view.Menu;
import android.view.MenuInflater;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.controller.TGMenuBase;
import org.herac.tuxguitar.android.view.dialog.track.TGTrackChannelDialogController;
import org.herac.tuxguitar.android.view.dialog.track.TGTrackNameDialogController;
import org.herac.tuxguitar.android.view.dialog.track.TGTrackStringCountDialogController;
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

public class TGTrackMenu extends TGMenuBase {
	
	public TGTrackMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_track, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(Menu menu) {
		TGContext context = findContext();
		TGCaret caret = TGSongViewController.getInstance(context).getCaret();
		TGTrack track = caret.getTrack();
		boolean running = MidiPlayer.getInstance(context).isRunning();
		boolean percussion = caret.getSongManager().isPercussionChannel(caret.getSong(), track.getChannelId());
		
		this.initializeItem(menu, R.id.action_track_add, this.createActionProcessor(TGAddNewTrackAction.NAME), !running);
		this.initializeItem(menu, R.id.action_track_remove, this.createActionProcessor(TGRemoveTrackAction.NAME), !running);
		this.initializeItem(menu, R.id.action_track_clone, this.createActionProcessor(TGCloneTrackAction.NAME), !running);
		this.initializeItem(menu, R.id.action_track_move_up, this.createActionProcessor(TGMoveTrackUpAction.NAME), !running);
		this.initializeItem(menu, R.id.action_track_move_down, this.createActionProcessor(TGMoveTrackDownAction.NAME), !running);
		this.initializeItem(menu, R.id.action_track_change_solo, this.createActionProcessor(TGChangeTrackSoloAction.NAME), !running, track.isSolo());
		this.initializeItem(menu, R.id.action_track_change_mute, this.createActionProcessor(TGChangeTrackMuteAction.NAME), !running, track.isMute());
		this.initializeItem(menu, R.id.action_track_set_name, new TGTrackNameDialogController(), !running);
		this.initializeItem(menu, R.id.action_track_set_channel, new TGTrackChannelDialogController(), !running);

		if( percussion ) {
			this.initializeItem(menu, R.id.action_track_change_string_count, new TGTrackStringCountDialogController(), !running);
		} else {
			this.initializeItem(menu, R.id.action_track_change_tuning, new TGTrackTuningDialogController(), !running);
		}
	}
}