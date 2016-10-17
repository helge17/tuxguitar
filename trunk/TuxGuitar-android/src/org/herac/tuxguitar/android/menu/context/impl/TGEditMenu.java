package org.herac.tuxguitar.android.menu.context.impl;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.android.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.context.TGContextMenuBase;
import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;
import org.herac.tuxguitar.player.base.MidiPlayer;

import android.view.ContextMenu;
import android.view.MenuInflater;

public class TGEditMenu extends TGContextMenuBase {
	
	public TGEditMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(ContextMenu menu, MenuInflater inflater) {
		menu.setHeaderTitle(R.string.menu_edit);
		inflater.inflate(R.menu.menu_edit, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(ContextMenu menu) {
		boolean running = MidiPlayer.getInstance(this.findContext()).isRunning();
		
		this.initializeItem(menu, R.id.menu_edit_undo, this.createActionProcessor(TGUndoAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_edit_redo, this.createActionProcessor(TGRedoAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_edit_set_voice_1, this.createActionProcessor(TGSetVoice1Action.NAME), true);
		this.initializeItem(menu, R.id.menu_edit_set_voice_2, this.createActionProcessor(TGSetVoice2Action.NAME), true);
	}
}