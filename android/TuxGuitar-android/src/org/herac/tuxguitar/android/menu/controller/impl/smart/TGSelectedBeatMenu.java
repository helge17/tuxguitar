package org.herac.tuxguitar.android.menu.controller.impl.smart;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.controller.TGMenuBase;
import org.herac.tuxguitar.android.view.dialog.stroke.TGStrokeDialogController;
import org.herac.tuxguitar.android.view.dialog.text.TGTextDialogController;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import org.herac.tuxguitar.editor.action.note.TGCleanBeatAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;

import android.view.Menu;
import android.view.MenuInflater;

public class TGSelectedBeatMenu extends TGMenuBase {
	
	public TGSelectedBeatMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_selected_beat, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		TGContext context = findContext();
		TGNote note = TGSongViewController.getInstance(context).getCaret().getSelectedNote();

		this.initializeItem(menu, R.id.action_change_tied_note, this.createActionProcessor(TGChangeTiedNoteAction.NAME), true, note != null && note.isTiedNote());
		this.initializeItem(menu, R.id.action_change_text, new TGTextDialogController(), true);
		this.initializeItem(menu, R.id.action_clean_beat, this.createActionProcessor(TGCleanBeatAction.NAME), true);
		this.initializeItem(menu, R.id.action_move_beats_left, this.createActionProcessor(TGMoveBeatsLeftAction.NAME), true);
		this.initializeItem(menu, R.id.action_move_beats_right, this.createActionProcessor(TGMoveBeatsRightAction.NAME), true);
		this.initializeItem(menu, R.id.action_set_voice_auto, this.createActionProcessor(TGSetVoiceAutoAction.NAME), true);
		this.initializeItem(menu, R.id.action_set_voice_down, this.createActionProcessor(TGSetVoiceDownAction.NAME), true);
		this.initializeItem(menu, R.id.action_set_voice_up, this.createActionProcessor(TGSetVoiceUpAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_stroke, new TGStrokeDialogController(), true);
	}
}
