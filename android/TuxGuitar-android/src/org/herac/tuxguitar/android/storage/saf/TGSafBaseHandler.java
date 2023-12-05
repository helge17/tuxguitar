package org.herac.tuxguitar.android.storage.saf;

import android.content.Intent;

import org.herac.tuxguitar.android.activity.TGActivityResultHandler;
import org.herac.tuxguitar.android.activity.TGActivityResultManager;

public abstract class TGSafBaseHandler implements TGActivityResultHandler {

	private TGSafProvider provider;
	private int requestCode;

	public TGSafBaseHandler(TGSafProvider provider) {
		this.provider = provider;
		this.requestCode = this.getResultManager().createRequestCode();
		this.getResultManager().addHandler(this.requestCode, this);
	}

	public void onActivityResult(int resultCode, Intent data) {
		this.getResultManager().removeHandler(this.requestCode, this);
	}

	public TGActivityResultManager getResultManager() {
		return this.getProvider().getActivity().getResultManager();
	}

	public TGSafProvider getProvider() {
		return this.provider;
	}

	public int getRequestCode() {
		return this.requestCode;
	}
}
