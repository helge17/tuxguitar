package org.herac.tuxguitar.android.menu.context.impl;

import android.view.Menu;
import android.view.MenuInflater;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.android.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.context.TGMenuBase;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasureCopyDialogController;
import org.herac.tuxguitar.android.view.dialog.measure.TGMeasurePasteDialogController;
import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;
import org.herac.tuxguitar.editor.clipboard.TGClipboard;
import org.herac.tuxguitar.player.base.MidiPlayer;

public class TGEditMenu extends TGMenuBase {
	
	public TGEditMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_edit, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(Menu menu) {
		boolean running = MidiPlayer.getInstance(this.findContext()).isRunning();
		
		this.initializeItem(menu, R.id.menu_edit_undo, this.createActionProcessor(TGUndoAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_edit_redo, this.createActionProcessor(TGRedoAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_edit_set_voice_1, this.createActionProcessor(TGSetVoice1Action.NAME), true);
		this.initializeItem(menu, R.id.menu_edit_set_voice_2, this.createActionProcessor(TGSetVoice2Action.NAME), true);
		this.initializeItem(menu, R.id.menu_edit_copy, new TGMeasureCopyDialogController(), !running);
		this.initializeItem(menu, R.id.menu_edit_paste, new TGMeasurePasteDialogController(), !running && TGClipboard.getInstance(findContext()).getData() != null);
	}
}