package org.herac.tuxguitar.android.activity;

import android.support.v7.app.ActionBar;

public class TGActivityActionBarController {

	private TGActivity activity;

	public TGActivityActionBarController(TGActivity activity) {
		this.activity = activity;
	}

	public ActionBar getActionBar() {
		return this.activity.getSupportActionBar();
	}

	public void setHomeButtonEnabled(boolean enabled) {
		this.getActionBar().setHomeButtonEnabled(enabled);
	}

	public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
		this.getActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp);
	}

	public void setDisplayShowTitleEnabled(boolean showTitle) {
		this.getActionBar().setDisplayShowTitleEnabled(showTitle);
	}

	public void setDisplayShowHomeEnabled(boolean showHome) {
		this.getActionBar().setDisplayShowHomeEnabled(showHome);
	}

	public void setDisplayUseLogoEnabled(boolean useLogo) {
		this.getActionBar().setDisplayUseLogoEnabled(useLogo);
	}

	public void setLogo(int resId) {
		this.getActionBar().setLogo(resId);
	}

	public void setTitle(int resId) {
		this.getActionBar().setTitle(resId);
	}

	public void setTitle(CharSequence title) {
		this.getActionBar().setTitle(title);
	}
}
