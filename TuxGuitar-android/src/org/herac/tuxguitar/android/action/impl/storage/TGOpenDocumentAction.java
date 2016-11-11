package org.herac.tuxguitar.android.action.impl.storage;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.storage.TGStorageManager;
import org.herac.tuxguitar.util.TGContext;

public class TGOpenDocumentAction extends TGActionBase {

	public static final String NAME = "action.storage.open-document";

	public TGOpenDocumentAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		TGStorageManager.getInstance(this.getContext()).openDocument();
	}
}