package org.herac.tuxguitar.android.action;

import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGActionBase extends org.herac.tuxguitar.editor.action.TGActionBase {

	public TGActionBase(TGContext context, String name) {
		super(context, name);
	}
	
	public TGSongViewController getEditor() {
		return TGSongViewController.getInstance(this.getContext());
	}
}
