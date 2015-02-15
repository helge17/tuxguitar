package org.herac.tuxguitar.jack.connection;

import org.herac.tuxguitar.jack.JackPortRegisterListener;
import org.herac.tuxguitar.util.TGContext;

public class JackConnectionListener implements JackPortRegisterListener {
	
	private JackConnectionProcess jackConnectionProcess;
	
	public JackConnectionListener(TGContext context, JackConnectionManager jackConnectionManager){
		this.jackConnectionProcess = new JackConnectionProcess(context, jackConnectionManager);
	}

	public void onPortRegistered() {
		this.jackConnectionProcess.process();
	}
}
