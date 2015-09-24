package org.herac.tuxguitar.android.view.tool;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.android.activity.R;

import android.content.Context;
import android.util.AttributeSet;

public class TGTransportToolView extends TGToolView {
	
	public TGTransportToolView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void addListeners() {
		this.findViewById(R.id.menu_transport_play).setOnClickListener(this.createActionListener(TGTransportPlayAction.NAME));
		this.findViewById(R.id.menu_transport_stop).setOnClickListener(this.createActionListener(TGTransportPlayAction.NAME));
	}
	
	public void updateItems() {
		boolean running = TuxGuitar.getInstance(this.findContext()).getPlayer().isRunning();
		
		this.updateItem(R.id.menu_transport_stop, running);
	}
}
