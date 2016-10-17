package org.herac.tuxguitar.android.menu.context.impl;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.context.TGContextMenuBase;
import org.herac.tuxguitar.android.view.dialog.bend.TGBendDialogController;
import org.herac.tuxguitar.android.view.dialog.grace.TGGraceDialogController;
import org.herac.tuxguitar.android.view.dialog.harmonic.TGHarmonicDialogController;
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
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;

import android.view.ContextMenu;
import android.view.MenuInflater;

public class TGEffectMenu extends TGContextMenuBase {
	
	public TGEffectMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(ContextMenu menu, MenuInflater inflater) {
		menu.setHeaderTitle(R.string.menu_effect);
		inflater.inflate(R.menu.menu_effect, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(ContextMenu menu) {
		TGContext context = findContext();
		TGNote note = TGSongViewController.getInstance(context).getCaret().getSelectedNote();
		boolean running = MidiPlayer.getInstance(context).isRunning();
		
		this.initializeItem(menu, R.id.menu_effect_vibrato, this.createActionProcessor(TGChangeVibratoNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_dead_note, this.createActionProcessor(TGChangeDeadNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_slide, this.createActionProcessor(TGChangeSlideNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_hammer, this.createActionProcessor(TGChangeHammerNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_ghost_note, this.createActionProcessor(TGChangeGhostNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_accentuated_note, this.createActionProcessor(TGChangeAccentuatedNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_heavy_accentuated_note, this.createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_palm_mute, this.createActionProcessor(TGChangePalmMuteAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_let_ring, this.createActionProcessor(TGChangeLetRingAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_staccato, this.createActionProcessor(TGChangeStaccatoAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_tapping, this.createActionProcessor(TGChangeTappingAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_slapping, this.createActionProcessor(TGChangeSlappingAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_popping, this.createActionProcessor(TGChangePoppingAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_fade_in, this.createActionProcessor(TGChangeFadeInAction.NAME), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_bend, new TGBendDialogController(), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_tremolo_bar, new TGTremoloBarDialogController(), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_grace, new TGGraceDialogController(), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_harmonic, new TGHarmonicDialogController(), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_trill, new TGTrillDialogController(), !running && note != null);
		this.initializeItem(menu, R.id.menu_effect_tremolo_picking, new TGTremoloPickingDialogController(), !running && note != null);
	}
}
