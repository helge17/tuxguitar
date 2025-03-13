package app.tuxguitar.android.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.transport.TGTransport;
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
