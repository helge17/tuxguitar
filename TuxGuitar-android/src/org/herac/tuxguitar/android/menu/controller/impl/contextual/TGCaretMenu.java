package org.herac.tuxguitar.android.menu.controller.impl.contextual;

import android.view.Menu;
import android.view.MenuInflater;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.caret.TGGoDownAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoUpAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.controller.TGMenuBase;
import org.herac.tuxguitar.player.base.MidiPlayer;

public class TGCaretMenu extends TGMenuBase {
	
	public TGCaretMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_caret, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(Menu menu) {
		boolean running = MidiPlayer.getInstance(this.findContext()).isRunning();
		
		this.initializeItem(menu, R.id.action_go_left, this.createActionProcessor(TGGoLeftAction.NAME), !running);
		this.initializeItem(menu, R.id.action_go_right, this.createActionProcessor(TGGoRightAction.NAME), !running);
		this.initializeItem(menu, R.id.action_go_up, this.createActionProcessor(TGGoUpAction.NAME), !running);
		this.initializeItem(menu, R.id.action_go_down, this.createActionProcessor(TGGoDownAction.NAME), !running);
	}
}