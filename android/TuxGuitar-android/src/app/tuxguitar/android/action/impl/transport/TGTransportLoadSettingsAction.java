package app.tuxguitar.android.action.impl.transport;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.transport.TGTransportAdapter;
import app.tuxguitar.util.TGContext;

public class TGTransportLoadSettingsAction extends TGActionBase{

	public static final String NAME = "action.transport.load-settings";

	public TGTransportLoadSettingsAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGTransportAdapter.getInstance(getContext()).loadSettings();
	}
}
