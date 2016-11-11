package org.herac.tuxguitar.android.view.dialog.chooser;

import android.app.Activity;

import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.android.view.dialog.TGDialogUtil;

public class TGChooserDialogController<T> implements TGDialogController {

	public static final String ATTRIBUTE_HANDLER = TGChooserDialogHandler.class.getName();
	public static final String ATTRIBUTE_OPTIONS = "options";
	public static final String ATTRIBUTE_TITLE = "title";

	public TGChooserDialogController() {
		super();
	}
	
	@Override
	public void showDialog(Activity activity, TGDialogContext context) {
		TGDialogUtil.showDialog(activity, new TGChooserDialog<T>(), context);
	}
}
