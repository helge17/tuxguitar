package org.herac.tuxguitar.android.menu.context.impl;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.context.TGContextMenuBase;

import android.view.ContextMenu;
import android.view.MenuInflater;

public class TGTransportMenu extends TGContextMenuBase {
	
	public TGTransportMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(ContextMenu menu, MenuInflater inflater) {
		menu.setHeaderTitle(R.string.menu_transport);
		inflater.inflate(R.menu.menu_transport, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(ContextMenu menu) {
		boolean running = TuxGuitar.getInstance(this.findContext()).getPlayer().isRunning();
		
		this.initializeItem(menu, R.id.menu_transport_play, this.createActionProcessor(TGTransportPlayAction.NAME), true);
		this.initializeItem(menu, R.id.menu_transport_stop, this.createActionProcessor(TGTransportPlayAction.NAME), running);
		
		menu.findItem(R.id.menu_transport_play).setTitle(running ? R.string.menu_transport_pause : R.string.menu_transport_play);
	}
}