package org.herac.tuxguitar.android.properties;

import android.app.Activity;
import android.content.SharedPreferences;

public abstract class TGSharedPreferencesHandler {
	
	private Activity activity;
	private String module;
	private String resource;
	
	public TGSharedPreferencesHandler(Activity activity, String module, String resource) {
		this.activity = activity;
		this.module = module;
		this.resource = resource;
	}
	
	public SharedPreferences getSharedPreferences() {
		return this.activity.getSharedPreferences(this.module + "-" + this.resource, 0);
	}
}
