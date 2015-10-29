package org.herac.tuxguitar.android.application;

import org.herac.tuxguitar.util.TGContext;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.view.View;

public final class TGApplicationUtil {
	
	public static TGContext findContext(Application application) {
		return ((TGApplication) application).getContext();
	}
	
	public static TGContext findContext(Activity activity) {
		return TGApplicationUtil.findContext(activity.getApplication());
	}
	
	public static TGContext findContext(Fragment fragment) {
		return TGApplicationUtil.findContext(fragment.getActivity());
	}
	
	public static TGContext findContext(View view) {
		return TGApplicationUtil.findContext(view.getContext());
	}
	
	public static TGContext findContext(Context context) {
		return TGApplicationUtil.findContext((Application)context.getApplicationContext());
	}
}
