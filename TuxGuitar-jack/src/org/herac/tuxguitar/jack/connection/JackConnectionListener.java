package org.herac.tuxguitar.jack.connection;

import org.herac.tuxguitar.jack.JackPortRegisterListener;

public class JackConnectionListener implements JackPortRegisterListener {
	
	private JackConnectionProcess jackConnectionProcess;
	
	public JackConnectionListener(JackConnectionManager jackConnectionManager){
		this.jackConnectionProcess = new JackConnectionProcess(jackConnectionManager);
	}

	public void onPortRegistered() {
		this.jackConnectionProcess.process();
	}
}
