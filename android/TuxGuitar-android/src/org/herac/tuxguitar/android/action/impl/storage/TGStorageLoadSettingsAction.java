package org.herac.tuxguitar.android.action.impl.storage;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.storage.TGStorageManager;
import org.herac.tuxguitar.util.TGContext;

public class TGStorageLoadSettingsAction extends TGActionBase{

	public static final String NAME = "action.storage.load-settings";

	public TGStorageLoadSettingsAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGStorageManager.getInstance(getContext()).loadSettings();
	}
}
