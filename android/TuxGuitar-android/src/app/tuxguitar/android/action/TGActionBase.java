package app.tuxguitar.android.action;

import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.util.TGContext;

public abstract class TGActionBase extends app.tuxguitar.editor.action.TGActionBase {

	public TGActionBase(TGContext context, String name) {
		super(context, name);
	}

	public TGSongViewController getEditor() {
		return TGSongViewController.getInstance(this.getContext());
	}
}
