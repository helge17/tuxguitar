package org.herac.tuxguitar.app.action.listener.transport;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionInterceptor;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;

public class TGStopTransportInterceptor implements TGActionInterceptor {
	
	private TGContext context;
	private List<String> actionIds;
	
	public TGStopTransportInterceptor(TGContext context){
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
		if( this.containsActionId(id) && MidiPlayer.getInstance(this.context).isRunning() ) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGTransportStopAction.NAME);
			tgActionProcessor.setOnFinish(executeInterceptedActionThread(id, context));
			tgActionProcessor.process();
			
			return true;
		}
		return false;
	}
	
	public Runnable executeInterceptedActionThread(final String actionId, final TGActionContext context) {
		return new Runnable() {
			public void run() {
				new Thread(new Runnable() {
					public void run() {
						executeInterceptedAction(actionId, context);
					}
				}).start();
			}
		};
	}
	
	public void executeInterceptedAction(String actionId, TGActionContext context) {
		TGActionManager tgActionManager = TGActionManager.getInstance(this.context);
		tgActionManager.execute(actionId, context);
	}
}
