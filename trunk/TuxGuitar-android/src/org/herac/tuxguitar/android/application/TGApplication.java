package org.herac.tuxguitar.android.application;

import org.herac.tuxguitar.util.TGContext;

import android.app.Application;

public class TGApplication extends Application {
	
	private TGContext context;
	
	public TGApplication() {
		this.context = new TGContext();
	}
	
	public TGContext getContext() {
		return context;
	}
}
