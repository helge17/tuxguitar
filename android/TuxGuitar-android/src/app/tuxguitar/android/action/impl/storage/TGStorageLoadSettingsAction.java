package app.tuxguitar.android.action.impl.storage;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.storage.TGStorageManager;
import app.tuxguitar.util.TGContext;

public class TGStorageLoadSettingsAction extends TGActionBase{

	public static final String NAME = "action.storage.load-settings";

	public TGStorageLoadSettingsAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGStorageManager.getInstance(getContext()).loadSettings();
	}
}
