package org.herac.tuxguitar.android.properties;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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
		return this.activity.getSharedPreferences(getPreferencesPrefix() + "." + this.module + "-" + this.resource, 0);
	}

	public String getPreferencesPrefix() {
		StringBuilder prefix = new StringBuilder("tuxguitar");
		try {
			PackageInfo packageInfo = this.activity.getPackageManager().getPackageInfo(this.activity.getPackageName(), 0);
			if( packageInfo != null ) {
				prefix.append("-" + packageInfo.versionCode);
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return prefix.toString();
	}
}
