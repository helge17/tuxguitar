package org.herac.tuxguitar.app.action.listener.transport;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionInterceptor;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;

public class TGDisableOnPlayInterceptor implements TGActionInterceptor {
	
	private TGContext context;
	private List<String> actionIds;
	
	public TGDisableOnPlayInterceptor(TGContext context){
		this.context = context;
		this.actionIds = new ArrayList<String>();
	}
	
	public boolean containsActionId(String id) {
		return this.actionIds.contains(id);
	}
	
	public void addActionId(String id) {
		this.actionIds.add(id);
	}
	
	public void removeActionId(String id) {
		this.actionIds.remove(id);
	}
	
	public boolean intercept(String id, TGActionContext context) throws TGActionException {
		boolean intercepted = true;
		boolean running = MidiPlayer.getInstance(this.context).isRunning();
		if(!running || !this.containsActionId(id)){
			intercepted = false;
		}
		
		if( intercepted ){
			TuxGuitar.getInstance().updateCache(true);
		}
		
		return intercepted;
	}
}
