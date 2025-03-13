package app.tuxguitar.android.menu.controller.impl.smart;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.dialog.track.TGTrackChannelDialogController;
import app.tuxguitar.android.view.dialog.track.TGTrackNameDialogController;
import app.tuxguitar.android.view.dialog.track.TGTrackStringCountDialogController;
import app.tuxguitar.android.view.dialog.track.TGTrackTuningDialogController;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import app.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import app.tuxguitar.editor.action.track.TGCloneTrackAction;
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGSelectedTrackMenu extends TGMenuBase {

	public TGSelectedTrackMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_selected_track, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		TGContext context = findContext();
		TGCaret caret = TGSongViewController.getInstance(context).getCaret();
		TGSong song = caret.getSong();
		TGTrack track = caret.getTrack();

		this.initializeItem(menu, R.id.action_track_clone, this.createActionProcessor(TGCloneTrackAction.NAME), true);
		this.initializeItem(menu, R.id.action_track_change_solo, this.createActionProcessor(TGChangeTrackSoloAction.NAME), true, track.isSolo());
		this.initializeItem(menu, R.id.action_track_change_mute, this.createActionProcessor(TGChangeTrackMuteAction.NAME), true, track.isMute());
		this.initializeItem(menu, R.id.action_track_set_name, new TGTrackNameDialogController(), true);
		this.initializeItem(menu, R.id.action_track_set_channel, new TGTrackChannelDialogController(), true);

		if( song.countTracks() > 1 ) {
			this.initializeItem(menu, R.id.action_track_remove, this.createActionProcessor(TGRemoveTrackAction.NAME), true);
			this.initializeItem(menu, R.id.action_track_move_up, this.createActionProcessor(TGMoveTrackUpAction.NAME), true);
			this.initializeItem(menu, R.id.action_track_move_down, this.createActionProcessor(TGMoveTrackDownAction.NAME), true);
		}
		if( caret.getSongManager().isPercussionChannel(song, track.getChannelId()) ) {
			this.initializeItem(menu, R.id.action_track_change_string_count, new TGTrackStringCountDialogController(), true);
		} else {
			this.initializeItem(menu, R.id.action_track_change_tuning, new TGTrackTuningDialogController(), true);
		}
	}
}
