package org.herac.tuxguitar.android.view.tool;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.menu.context.impl.TGDurationMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGEffectMenu;
import org.herac.tuxguitar.android.menu.context.impl.TGVelocityMenu;
import org.herac.tuxguitar.android.view.dialog.stroke.TGStrokeDialogController;
import org.herac.tuxguitar.android.view.dialog.text.TGTextDialogController;
import org.herac.tuxguitar.android.view.tablature.TGCaret;
import org.herac.tuxguitar.android.view.tablature.TGSongView;
import org.herac.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import org.herac.tuxguitar.editor.action.note.TGCleanBeatAction;
import org.herac.tuxguitar.editor.action.note.TGDecrementNoteSemitoneAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import org.herac.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import org.herac.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveUnusedVoiceAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import org.herac.tuxguitar.editor.action.note.TGShiftNoteDownAction;
import org.herac.tuxguitar.editor.action.note.TGShiftNoteUpAction;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.util.TGContext;

import android.content.Context;
import android.util.AttributeSet;

public class TGBeatToolView extends TGToolView {
	
	public TGBeatToolView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void addListeners() {
		this.findViewById(R.id.menu_beat_change_tied_note).setOnClickListener(this.createActionListener(TGChangeTiedNoteAction.NAME));
		this.findViewById(R.id.menu_beat_clean_beat).setOnClickListener(this.createActionListener(TGCleanBeatAction.NAME));
		this.findViewById(R.id.menu_beat_decrement_note_semitone).setOnClickListener(this.createActionListener(TGDecrementNoteSemitoneAction.NAME));
		this.findViewById(R.id.menu_beat_delete_note_or_rest).setOnClickListener(this.createActionListener(TGDeleteNoteOrRestAction.NAME));
		this.findViewById(R.id.menu_beat_increment_note_semitone).setOnClickListener(this.createActionListener(TGIncrementNoteSemitoneAction.NAME));
		this.findViewById(R.id.menu_beat_insert_rest_beat).setOnClickListener(this.createActionListener(TGInsertRestBeatAction.NAME));
		this.findViewById(R.id.menu_beat_move_beats_left).setOnClickListener(this.createActionListener(TGMoveBeatsLeftAction.NAME));
		this.findViewById(R.id.menu_beat_move_beats_right).setOnClickListener(this.createActionListener(TGMoveBeatsRightAction.NAME));
		this.findViewById(R.id.menu_beat_remove_unused_voice).setOnClickListener(this.createActionListener(TGRemoveUnusedVoiceAction.NAME));
		this.findViewById(R.id.menu_beat_set_voice_auto).setOnClickListener(this.createActionListener(TGSetVoiceAutoAction.NAME));
		this.findViewById(R.id.menu_beat_set_voice_down).setOnClickListener(this.createActionListener(TGSetVoiceDownAction.NAME));
		this.findViewById(R.id.menu_beat_set_voice_up).setOnClickListener(this.createActionListener(TGSetVoiceUpAction.NAME));
		this.findViewById(R.id.menu_beat_shift_note_down).setOnClickListener(this.createActionListener(TGShiftNoteDownAction.NAME));
		this.findViewById(R.id.menu_beat_shift_note_up).setOnClickListener(this.createActionListener(TGShiftNoteUpAction.NAME));
		this.findViewById(R.id.menu_beat_stroke).setOnClickListener(this.createDialogActionListener(new TGStrokeDialogController()));
		this.findViewById(R.id.menu_beat_edit_text).setOnClickListener(this.createDialogActionListener(new TGTextDialogController()));
		this.findViewById(R.id.menu_duration).setOnClickListener(this.createContextMenuActionListener(new TGDurationMenu(findActivity())));
		this.findViewById(R.id.menu_velocity).setOnClickListener(this.createContextMenuActionListener(new TGVelocityMenu(findActivity())));
		this.findViewById(R.id.menu_effect).setOnClickListener(this.createContextMenuActionListener(new TGEffectMenu(findActivity())));
	}
	
	public void updateItems() {
		TGContext context = findContext();
		TGCaret caret = TGSongView.getInstance(context).getCaret();
		TGNote note = caret.getSelectedNote();
		boolean restBeat = caret.isRestBeatSelected();
		boolean running = TuxGuitar.getInstance(context).getPlayer().isRunning();
		
		this.updateCheckItem(R.id.menu_beat_change_tied_note, !running , (note != null && note.isTiedNote()) );
		
		this.updateItem(R.id.menu_beat_clean_beat, !running);
		this.updateItem(R.id.menu_beat_decrement_note_semitone, (!running && note != null));
		this.updateItem(R.id.menu_beat_delete_note_or_rest, !running);
		this.updateItem(R.id.menu_beat_increment_note_semitone, (!running && note != null));
		this.updateItem(R.id.menu_beat_insert_rest_beat, !running);
		this.updateItem(R.id.menu_beat_move_beats_left, !running);
		this.updateItem(R.id.menu_beat_move_beats_right, !running);
		this.updateItem(R.id.menu_beat_remove_unused_voice, !running);
		this.updateItem(R.id.menu_beat_set_voice_auto, (!running && !restBeat));
		this.updateItem(R.id.menu_beat_set_voice_down, (!running && !restBeat));
		this.updateItem(R.id.menu_beat_set_voice_up, (!running && !restBeat));
		this.updateItem(R.id.menu_beat_shift_note_down, (!running && note != null));
		this.updateItem(R.id.menu_beat_shift_note_up, (!running && note != null));
		this.updateItem(R.id.menu_beat_stroke, !running);
		this.updateItem(R.id.menu_beat_edit_text, !running);
		this.updateItem(R.id.menu_duration, !running);
		this.updateItem(R.id.menu_velocity, !running);
		this.updateItem(R.id.menu_effect, !running);
	}
}
