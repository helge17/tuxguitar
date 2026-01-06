package app.tuxguitar.android.menu.controller.impl.contextual;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.action.impl.track.TGGoFirstTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoPreviousTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoNextTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoLastTrackAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.dialog.track.TGTrackChannelDialogController;
import app.tuxguitar.android.view.dialog.track.TGTrackNameDialogController;
import app.tuxguitar.android.view.dialog.track.TGTrackStringCountDialogController;
import app.tuxguitar.android.view.dialog.track.TGTrackTuningDialogController;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.editor.action.track.TGAddNewTrackAction;
import app.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import app.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import app.tuxguitar.editor.action.track.TGCloneTrackAction;
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

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
		int tracks = track.getSong().countTracks();
		boolean isFirst = (track.getNumber() == 1);
		boolean isLast = (track.getNumber() == tracks);
		boolean running = MidiPlayer.getInstance(context).isRunning();
		boolean percussion = caret.getSongManager().isPercussionChannel(caret.getSong(), track.getChannelId());

		this.initializeItem(menu, R.id.action_track_first, this.createActionProcessor(TGGoFirstTrackAction.NAME), !isFirst);
		this.initializeItem(menu, R.id.action_track_previous, this.createActionProcessor(TGGoPreviousTrackAction.NAME), !isFirst);
		this.initializeItem(menu, R.id.action_track_next, this.createActionProcessor(TGGoNextTrackAction.NAME), !isLast);
		this.initializeItem(menu, R.id.action_track_last, this.createActionProcessor(TGGoLastTrackAction.NAME), !isLast);
		this.initializeItem(menu, R.id.action_track_add, this.createActionProcessor(TGAddNewTrackAction.NAME), !running);
		this.initializeItem(menu, R.id.action_track_clone, this.createActionProcessor(TGCloneTrackAction.NAME), !running);
		this.initializeItem(menu, R.id.action_track_remove, this.createActionProcessor(TGRemoveTrackAction.NAME), !running);
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
