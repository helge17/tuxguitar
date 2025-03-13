package app.tuxguitar.android.menu.controller.impl.smart;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.dialog.bend.TGBendDialogController;
import app.tuxguitar.android.view.dialog.grace.TGGraceDialogController;
import app.tuxguitar.android.view.dialog.harmonic.TGHarmonicDialogController;
import app.tuxguitar.android.view.dialog.pickstroke.TGPickStrokeDialogController;
import app.tuxguitar.android.view.dialog.stroke.TGStrokeDialogController;
import app.tuxguitar.android.view.dialog.text.TGTextDialogController;
import app.tuxguitar.android.view.dialog.tremoloBar.TGTremoloBarDialogController;
import app.tuxguitar.android.view.dialog.tremoloPicking.TGTremoloPickingDialogController;
import app.tuxguitar.android.view.dialog.trill.TGTrillDialogController;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.editor.action.effect.TGChangeAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeDeadNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeFadeInAction;
import app.tuxguitar.editor.action.effect.TGChangeGhostNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHammerNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeLetRingAction;
import app.tuxguitar.editor.action.effect.TGChangePalmMuteAction;
import app.tuxguitar.editor.action.effect.TGChangePoppingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlappingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlideNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeStaccatoAction;
import app.tuxguitar.editor.action.effect.TGChangeTappingAction;
import app.tuxguitar.editor.action.effect.TGChangeVibratoNoteAction;
import app.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import app.tuxguitar.editor.action.note.TGCleanBeatAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import app.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import app.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import app.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGContext;

public class TGSelectedNoteMenu extends TGMenuBase {

	public TGSelectedNoteMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_selected_note, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		TGContext context = findContext();
		TGNote note = TGSongViewController.getInstance(context).getCaret().getSelectedNote();

		this.initializeItem(menu, R.id.action_change_vibrato, this.createActionProcessor(TGChangeVibratoNoteAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_dead_note, this.createActionProcessor(TGChangeDeadNoteAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_slide, this.createActionProcessor(TGChangeSlideNoteAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_hammer, this.createActionProcessor(TGChangeHammerNoteAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_ghost_note, this.createActionProcessor(TGChangeGhostNoteAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_accentuated_note, this.createActionProcessor(TGChangeAccentuatedNoteAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_heavy_accentuated_note, this.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_palm_mute, this.createActionProcessor(TGChangePalmMuteAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_let_ring, this.createActionProcessor(TGChangeLetRingAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_staccato, this.createActionProcessor(TGChangeStaccatoAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_tapping, this.createActionProcessor(TGChangeTappingAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_slapping, this.createActionProcessor(TGChangeSlappingAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_popping, this.createActionProcessor(TGChangePoppingAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_fade_in, this.createActionProcessor(TGChangeFadeInAction.NAME), true);
		this.initializeItem(menu, R.id.action_change_bend, new TGBendDialogController(), true);
		this.initializeItem(menu, R.id.action_change_tremolo_bar, new TGTremoloBarDialogController(), true);
		this.initializeItem(menu, R.id.action_change_grace, new TGGraceDialogController(), true);
		this.initializeItem(menu, R.id.action_change_harmonic, new TGHarmonicDialogController(), true);
		this.initializeItem(menu, R.id.action_change_trill, new TGTrillDialogController(), true);
		this.initializeItem(menu, R.id.action_change_tremolo_picking, new TGTremoloPickingDialogController(), true);
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
