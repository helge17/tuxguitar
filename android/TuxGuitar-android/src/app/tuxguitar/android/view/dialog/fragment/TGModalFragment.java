package org.herac.tuxguitar.android.view.dialog.fragment;

import org.herac.tuxguitar.android.fragment.TGCachedFragment;
import org.herac.tuxguitar.android.view.dialog.TGDialogContext;

public abstract class TGModalFragment extends TGCachedFragment {

	public TGModalFragment(int layout) {
		super(layout);
	}

	public String getDialogContextKey() {
		return (TGDialogContext.class.getName() + "-" + this.getClass().getName());
	}

	public TGDialogContext getDialogContext() {
		return this.findContext().getAttribute(this.getDialogContextKey());
	}

	public void destroyDialogContext() {
		this.findContext().removeAttribute(this.getDialogContextKey());
	}

	public <T> T getAttribute(String key){
		TGDialogContext dialogContext = this.getDialogContext();
		if( dialogContext != null ) {
			return dialogContext.getAttribute(key);
		}
		return null;
	}

	public void close() {
		this.findActivity().callBackAction();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		this.destroyDialogContext();
	}
}

