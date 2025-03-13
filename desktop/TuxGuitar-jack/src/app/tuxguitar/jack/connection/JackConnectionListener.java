package app.tuxguitar.jack.connection;

import app.tuxguitar.jack.JackPortRegisterListener;
import app.tuxguitar.util.TGContext;

public class JackConnectionListener implements JackPortRegisterListener {

	private JackConnectionProcess jackConnectionProcess;

	public JackConnectionListener(TGContext context, JackConnectionManager jackConnectionManager){
		this.jackConnectionProcess = new JackConnectionProcess(context, jackConnectionManager);
	}

	public void onPortRegistered() {
		this.jackConnectionProcess.process();
	}
}
