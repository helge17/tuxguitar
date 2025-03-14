package app.tuxguitar.app.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGTransportStopAction extends TGActionBase{

	public static final String NAME = "action.transport.stop";

	public TGTransportStopAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGTransport.getInstance(getContext()).stop();
	}
}
