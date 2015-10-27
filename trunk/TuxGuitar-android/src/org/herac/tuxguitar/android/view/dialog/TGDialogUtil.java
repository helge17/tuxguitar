package org.herac.tuxguitar.android.view.dialog;

import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.util.TGContext;

import android.app.Activity;

public final class TGDialogUtil {
	
	public static void showDialog(Activity activity, TGDialog dialog, TGDialogContext dialogContext) {
		TGContext tgContext = TGApplicationUtil.findContext(activity);
		tgContext.setAttribute(dialog.getDialogContextKey(), dialogContext);
		
        dialog.show(activity.getFragmentManager(), "NoticeDialogFragment");
	}
}
