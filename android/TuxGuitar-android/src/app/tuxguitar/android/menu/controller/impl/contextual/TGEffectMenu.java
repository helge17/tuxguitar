package app.tuxguitar.android.menu.controller.impl.contextual;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.dialog.bend.TGBendDialogController;
import app.tuxguitar.android.view.dialog.grace.TGGraceDialogController;
import app.tuxguitar.android.view.dialog.harmonic.TGHarmonicDialogController;
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
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.util.TGContext;

public class TGEffectMenu extends TGMenuBase {

	public TGEffectMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_effect, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		TGContext context = findContext();
		TGNote note = TGSongViewController.getInstance(context).getCaret().getSelectedNote();
		boolean running = MidiPlayer.getInstance(context).isRunning();

		this.initializeItem(menu, R.id.action_change_vibrato, this.createActionProcessor(TGChangeVibratoNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_dead_note, this.createActionProcessor(TGChangeDeadNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_slide, this.createActionProcessor(TGChangeSlideNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_hammer, this.createActionProcessor(TGChangeHammerNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_ghost_note, this.createActionProcessor(TGChangeGhostNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_accentuated_note, this.createActionProcessor(TGChangeAccentuatedNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_heavy_accentuated_note, this.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_palm_mute, this.createActionProcessor(TGChangePalmMuteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_let_ring, this.createActionProcessor(TGChangeLetRingAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_staccato, this.createActionProcessor(TGChangeStaccatoAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_tapping, this.createActionProcessor(TGChangeTappingAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_slapping, this.createActionProcessor(TGChangeSlappingAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_popping, this.createActionProcessor(TGChangePoppingAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_fade_in, this.createActionProcessor(TGChangeFadeInAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.action_change_bend, new TGBendDialogController(), !running && note != null);
		this.initializeItem(menu, R.id.action_change_tremolo_bar, new TGTremoloBarDialogController(), !running && note != null);
		this.initializeItem(menu, R.id.action_change_grace, new TGGraceDialogController(), !running && note != null);
		this.initializeItem(menu, R.id.action_change_harmonic, new TGHarmonicDialogController(), !running && note != null);
		this.initializeItem(menu, R.id.action_change_trill, new TGTrillDialogController(), !running && note != null);
		this.initializeItem(menu, R.id.action_change_tremolo_picking, new TGTremoloPickingDialogController(), !running && note != null);
	}
}
