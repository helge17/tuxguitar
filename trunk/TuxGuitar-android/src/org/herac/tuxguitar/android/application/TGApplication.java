package org.herac.tuxguitar.android.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class TGApplication extends MultiDexApplication {
	
	public TGApplication() {
		super();
	}

	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);

		MultiDex.install(this);
	}
}
