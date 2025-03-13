package app.tuxguitar.android.menu.controller.impl.contextual;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.dialog.pickstroke.TGPickStrokeDialogController;
import app.tuxguitar.android.view.dialog.stroke.TGStrokeDialogController;
import app.tuxguitar.android.view.dialog.text.TGTextDialogController;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import app.tuxguitar.editor.action.note.TGCleanBeatAction;
import app.tuxguitar.editor.action.note.TGDecrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import app.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import app.tuxguitar.editor.action.note.TGRemoveUnusedVoiceAction;
import app.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import app.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import app.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import app.tuxguitar.editor.action.note.TGShiftNoteDownAction;
import app.tuxguitar.editor.action.note.TGShiftNoteUpAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGContext;

public class TGBeatMenu extends TGMenuBase {

	public TGBeatMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_beat, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		TGContext context = findContext();
		TGCaret caret = TGSongViewController.getInstance(context).getCaret();
		TGNote note = caret.getSelectedNote();
		boolean restBeat = caret.isRestBeatSelected();
		boolean running = MidiPlayer.getInstance(context).isRunning();

		this.initializeItem(menu, R.id.action_change_tied_note, this.createActionProcessor(TGChangeTiedNoteAction.NAME), !running, note != null && note.isTiedNote());
		this.initializeItem(menu, R.id.action_clean_beat, this.createActionProcessor(TGCleanBeatAction.NAME), !running);
		this.initializeItem(menu, R.id.action_decrement_note_semitone, this.createActionProcessor(TGDecrementNoteSemitoneAction.NAME), (!running && note != null));
		this.initializeItem(menu, R.id.action_delete_note_or_rest, this.createActionProcessor(TGDeleteNoteOrRestAction.NAME), !running);
		this.initializeItem(menu, R.id.action_increment_note_semitone, this.createActionProcessor(TGIncrementNoteSemitoneAction.NAME), (!running && note != null));
		this.initializeItem(menu, R.id.action_insert_rest_beat, this.createActionProcessor(TGInsertRestBeatAction.NAME), !running);
		this.initializeItem(menu, R.id.action_move_beats_left, this.createActionProcessor(TGMoveBeatsLeftAction.NAME), !running);
		this.initializeItem(menu, R.id.action_move_beats_right, this.createActionProcessor(TGMoveBeatsRightAction.NAME), !running);
		this.initializeItem(menu, R.id.action_remove_unused_voice, this.createActionProcessor(TGRemoveUnusedVoiceAction.NAME), !running);
		this.initializeItem(menu, R.id.action_set_voice_auto, this.createActionProcessor(TGSetVoiceAutoAction.NAME), (!running && !restBeat));
		this.initializeItem(menu, R.id.action_set_voice_down, this.createActionProcessor(TGSetVoiceDownAction.NAME), (!running && !restBeat));
		this.initializeItem(menu, R.id.action_set_voice_up, this.createActionProcessor(TGSetVoiceUpAction.NAME), (!running && !restBeat));
		this.initializeItem(menu, R.id.action_shift_note_down, this.createActionProcessor(TGShiftNoteDownAction.NAME), (!running && note != null));
		this.initializeItem(menu, R.id.action_shift_note_up, this.createActionProcessor(TGShiftNoteUpAction.NAME), (!running && note != null));
		this.initializeItem(menu, R.id.action_change_stroke, new TGStrokeDialogController(), !running);
		this.initializeItem(menu, R.id.action_change_pickstroke, new TGPickStrokeDialogController(), !running);
		this.initializeItem(menu, R.id.action_change_text, new TGTextDialogController(), !running);
	}
}
