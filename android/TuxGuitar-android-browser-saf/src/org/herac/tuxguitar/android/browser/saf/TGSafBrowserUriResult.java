package org.herac.tuxguitar.android.browser.saf;

import android.app.Activity;
import android.content.Intent;

import org.herac.tuxguitar.android.activity.TGActivityResultHandler;
import org.herac.tuxguitar.android.activity.TGActivityResultManager;

public class TGSafBrowserUriResult implements TGActivityResultHandler {

	private TGSafBrowserUriRequest request;
	private int requestCode;

	public TGSafBrowserUriResult(TGSafBrowserUriRequest request) {
		this.request = request;
		this.requestCode = this.getResultManager().createRequestCode();
		this.getResultManager().addHandler(this.requestCode, this);
	}

	public void onActivityResult(int resultCode, Intent data) {
		this.getResultManager().removeHandler(this.requestCode, this);
		this.getRequest().onUriAccessGranted(resultCode == Activity.RESULT_OK ? data.getData() : null);
	}

	public TGActivityResultManager getResultManager() {
		return this.getRequest().getActivity().getResultManager();
	}

	public TGSafBrowserUriRequest getRequest() {
		return this.request;
	}

	public int getRequestCode() {
		return this.requestCode;
	}
}
