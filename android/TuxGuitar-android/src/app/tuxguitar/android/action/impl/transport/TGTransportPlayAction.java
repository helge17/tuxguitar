package app.tuxguitar.android.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.transport.TGTransport;
import app.tuxguitar.util.TGContext;

public class TGTransportPlayAction extends TGActionBase {

	public static final String NAME = "action.transport.play";

	public TGTransportPlayAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGTransport.getInstance(getContext()).play();
	}
}