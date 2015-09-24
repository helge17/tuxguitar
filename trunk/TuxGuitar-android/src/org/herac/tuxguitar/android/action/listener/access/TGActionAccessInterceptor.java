package org.herac.tuxguitar.android.action.listener.access;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionInterceptor;
import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.util.TGContext;

public class TGActionAccessInterceptor implements TGActionInterceptor {
	
	private TGContext context;
	private List<String> disableOnPlayingActionIds;
	
	public TGActionAccessInterceptor(TGContext context){
		this.context = context;
		this.disableOnPlayingActionIds = new ArrayList<String>();
	}
	
	public boolean isDisableOnPlayingAction(String id) {
		return this.disableOnPlayingActionIds.contains(id);
	}
	
	public void addDisableOnPlayingAction(String id) {
		this.disableOnPlayingActionIds.add(id);
	}
	
	public void removeDisableOnPlayingAction(String id) {
		this.disableOnPlayingActionIds.remove(id);
	}
	
	public boolean intercept(String id, TGActionContext context) throws TGActionException {
		boolean intercepted = true;
		
		TuxGuitar tuxguitar = TuxGuitar.getInstance(this.context);
		boolean running = tuxguitar.getPlayer().isRunning();
		if(!running || !this.isDisableOnPlayingAction(id)){
			intercepted = false;
		}
		
		if( intercepted ){
			tuxguitar.updateCache(true);
		}
		
		return intercepted;
	}
}
