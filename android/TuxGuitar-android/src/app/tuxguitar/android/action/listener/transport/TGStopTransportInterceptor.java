package app.tuxguitar.android.action.listener.transport;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.android.action.impl.transport.TGTransportStopAction;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;

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
		try {
			TGActionManager tgActionManager = TGActionManager.getInstance(this.context);
			tgActionManager.execute(actionId, context);
		} catch (TGActionException e) {
			e.printStackTrace();
		}
	}
}
