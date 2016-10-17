package org.herac.tuxguitar.android.menu.context.impl;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.caret.TGGoDownAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoUpAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.context.TGContextMenuBase;
import org.herac.tuxguitar.player.base.MidiPlayer;

import android.view.ContextMenu;
import android.view.MenuInflater;

public class TGCaretMenu extends TGContextMenuBase {
	
	public TGCaretMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(ContextMenu menu, MenuInflater inflater) {
		menu.setHeaderTitle(R.string.menu_caret);
		inflater.inflate(R.menu.menu_caret, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(ContextMenu menu) {
		boolean running = MidiPlayer.getInstance(this.findContext()).isRunning();
		
		this.initializeItem(menu, R.id.menu_caret_go_left, this.createActionProcessor(TGGoLeftAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_caret_go_right, this.createActionProcessor(TGGoRightAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_caret_go_up, this.createActionProcessor(TGGoUpAction.NAME), !running);
		this.initializeItem(menu, R.id.menu_caret_go_down, this.createActionProcessor(TGGoDownAction.NAME), !running);
	}
}