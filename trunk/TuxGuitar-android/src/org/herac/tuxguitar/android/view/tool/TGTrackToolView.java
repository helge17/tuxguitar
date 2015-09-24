package org.herac.tuxguitar.android.view.tool;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.action.impl.track.TGGoFirstTrackAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoLastTrackAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoNextTrackAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoPreviousTrackAction;
import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.view.dialog.track.TGTrackChannelDialogController;
import org.herac.tuxguitar.android.view.dialog.track.TGTrackNameDialogController;
import org.herac.tuxguitar.android.view.dialog.track.TGTrackTuningDialogController;
import org.herac.tuxguitar.editor.action.track.TGAddTrackAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import org.herac.tuxguitar.editor.action.track.TGCloneTrackAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import org.herac.tuxguitar.editor.action.track.TGRemoveTrackAction;

import android.content.Context;
import android.util.AttributeSet;

public class TGTrackToolView extends TGToolView {
	
	public TGTrackToolView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void addListeners() {
		this.findViewById(R.id.menu_track_go_first_track).setOnClickListener(this.createActionListener(TGGoFirstTrackAction.NAME));
		this.findViewById(R.id.menu_track_go_last_track).setOnClickListener(this.createActionListener(TGGoLastTrackAction.NAME));
		this.findViewById(R.id.menu_track_go_previous_track).setOnClickListener(this.createActionListener(TGGoPreviousTrackAction.NAME));
		this.findViewById(R.id.menu_track_go_next_track).setOnClickListener(this.createActionListener(TGGoNextTrackAction.NAME));
		
		this.findViewById(R.id.menu_track_add).setOnClickListener(this.createActionListener(TGAddTrackAction.NAME));
		this.findViewById(R.id.menu_track_remove).setOnClickListener(this.createActionListener(TGRemoveTrackAction.NAME));
		this.findViewById(R.id.menu_track_clone).setOnClickListener(this.createActionListener(TGCloneTrackAction.NAME));
		
		this.findViewById(R.id.menu_track_move_up).setOnClickListener(this.createActionListener(TGMoveTrackUpAction.NAME));
		this.findViewById(R.id.menu_track_move_down).setOnClickListener(this.createActionListener(TGMoveTrackDownAction.NAME));
		
		this.findViewById(R.id.menu_track_change_solo).setOnClickListener(this.createActionListener(TGChangeTrackSoloAction.NAME));
		this.findViewById(R.id.menu_track_change_mute).setOnClickListener(this.createActionListener(TGChangeTrackMuteAction.NAME));
		
		this.findViewById(R.id.menu_track_set_name).setOnClickListener(this.createDialogActionListener(new TGTrackNameDialogController()));
		this.findViewById(R.id.menu_track_set_channel).setOnClickListener(this.createDialogActionListener(new TGTrackChannelDialogController()));
		this.findViewById(R.id.menu_track_change_tuning).setOnClickListener(this.createDialogActionListener(new TGTrackTuningDialogController()));
	}
	
	public void updateItems() {
		boolean running = TuxGuitar.getInstance(this.findContext()).getPlayer().isRunning();
		
		this.updateItem(R.id.menu_track_go_first_track, !running);
		this.updateItem(R.id.menu_track_go_last_track, !running);
		this.updateItem(R.id.menu_track_go_previous_track, !running);
		this.updateItem(R.id.menu_track_go_next_track, !running);
		this.updateItem(R.id.menu_track_add, !running);
		this.updateItem(R.id.menu_track_remove, !running);
		this.updateItem(R.id.menu_track_clone, !running);
		this.updateItem(R.id.menu_track_move_up, !running);
		this.updateItem(R.id.menu_track_move_down, !running);
		this.updateItem(R.id.menu_track_change_solo, !running);
		this.updateItem(R.id.menu_track_change_mute, !running);
		this.updateItem(R.id.menu_track_set_name, !running);
		this.updateItem(R.id.menu_track_set_channel, !running);
		this.updateItem(R.id.menu_track_change_tuning, !running);
	}
}
