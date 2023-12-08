package org.herac.tuxguitar.jack.connection;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class JackConnectionProcess implements Runnable {
	
	private static final long WAIT_MILLIS = 500;
	
	private TGContext context;
	private JackConnectionManager jackConnectionManager;
	private boolean JackConnectionProcessWaiting;
	
	public JackConnectionProcess(TGContext context, JackConnectionManager jackConnectionManager){
		this.context = context;
		this.jackConnectionManager = jackConnectionManager;
		this.JackConnectionProcessWaiting = false;
	}
	
	public void run() {
		try {
			Thread.sleep(WAIT_MILLIS);
			
			this.JackConnectionProcessWaiting = false;
			
			if( this.jackConnectionManager.isJackClientOpen() && this.jackConnectionManager.isAutoConnectPorts() ){
				this.jackConnectionManager.connectAllPorts();
			}
		} catch (Throwable throwable) {
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}
	
	public void process() {
		if(!this.JackConnectionProcessWaiting ){
			this.JackConnectionProcessWaiting = true;
			
			Thread thread = new Thread(this);
			thread.start();
		}
	}
}
