package org.herac.tuxguitar.android.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.transport.TGTransport;
import org.herac.tuxguitar.util.TGContext;

public class TGTransportPlayAction extends TGActionBase {
	
	public static final String NAME = "action.transport.play";
	
	public TGTransportPlayAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGTransport.getInstance(getContext()).play();
	}
}