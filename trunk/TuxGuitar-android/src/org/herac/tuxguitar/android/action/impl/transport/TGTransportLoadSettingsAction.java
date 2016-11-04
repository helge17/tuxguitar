package org.herac.tuxguitar.android.action.impl.transport;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.transport.TGTransport;
import org.herac.tuxguitar.android.transport.TGTransportAdapter;
import org.herac.tuxguitar.util.TGContext;

public class TGTransportLoadSettingsAction extends TGActionBase{

	public static final String NAME = "action.transport.load-settings";

	public TGTransportLoadSettingsAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGTransportAdapter.getInstance(getContext()).loadSettings();
	}
}
