package org.herac.tuxguitar.android.application;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class TGApplication extends MultiDexApplication {
	
	public TGApplication() {
		super();
	}

	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);

		MultiDex.install(this);
	}
}
