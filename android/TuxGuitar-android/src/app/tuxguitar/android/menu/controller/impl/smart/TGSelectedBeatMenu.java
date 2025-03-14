package app.tuxguitar.android.menu.controller.impl.smart;

import app.tuxguitar.android.R;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.dialog.pickstroke.TGPickStrokeDialogController;
import app.tuxguitar.android.view.dialog.stroke.TGStrokeDialogController;
import app.tuxguitar.android.view.dialog.text.TGTextDialogController;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import app.tuxguitar.editor.action.note.TGCleanBeatAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import app.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import app.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import app.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGContext;

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
		this.initializeItem(menu, R.id.action_change_pickstroke, new TGPickStrokeDialogController(), true);
	}
}
