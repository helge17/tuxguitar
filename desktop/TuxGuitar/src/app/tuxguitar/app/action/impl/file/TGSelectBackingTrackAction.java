package app.tuxguitar.app.action.impl.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.dialog.backingtrack.TGBackingTrackDialog;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGSelectBackingTrackAction extends TGActionBase {

	public static final String NAME = "action.file.select-backing-track";

	public TGSelectBackingTrackAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext actionContext) {
		new TGBackingTrackDialog(getContext()).show();
	}
}
