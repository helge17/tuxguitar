package org.herac.tuxguitar.android.menu.controller.impl.smart;

import android.view.Menu;
import android.view.MenuInflater;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.controller.TGMenuBase;
import org.herac.tuxguitar.android.view.dialog.bend.TGBendDialogController;
import org.herac.tuxguitar.android.view.dialog.grace.TGGraceDialogController;
import org.herac.tuxguitar.android.view.dialog.harmonic.TGHarmonicDialogController;
import org.herac.tuxguitar.android.view.dialog.stroke.TGStrokeDialogController;
import org.herac.tuxguitar.android.view.dialog.text.TGTextDialogController;
import org.herac.tuxguitar.android.view.dialog.tremoloBar.TGTremoloBarDialogController;
import org.herac.tuxguitar.android.view.dialog.tremoloPicking.TGTremoloPickingDialogController;
import org.herac.tuxguitar.android.view.dialog.trill.TGTrillDialogController;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.editor.action.effect.TGChangeAccentuatedNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeDeadNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeFadeInAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeGhostNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeHammerNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeLetRingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangePalmMuteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangePoppingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeSlappingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeSlideNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeStaccatoAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeTappingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeVibratoNoteAction;
import org.herac.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import org.herac.tuxguitar.editor.action.note.TGCleanBeatAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;

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
	}
}
