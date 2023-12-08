package org.herac.tuxguitar.android.action.impl.storage;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.storage.TGStorageManager;
import org.herac.tuxguitar.util.TGContext;

public class TGSaveDocumentAsAction extends TGActionBase {

	public static final String NAME = "action.storage.save-document-as";

	public TGSaveDocumentAsAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		TGStorageManager.getInstance(this.getContext()).saveDocumentAs();
	}
}