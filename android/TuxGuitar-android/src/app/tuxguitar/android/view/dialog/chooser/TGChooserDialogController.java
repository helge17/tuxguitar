package org.herac.tuxguitar.android.view.dialog.chooser;

import org.herac.tuxguitar.android.view.dialog.fragment.TGDialogFragmentController;

public class TGChooserDialogController<T> extends TGDialogFragmentController<TGChooserDialog<T>> {

	public static final String ATTRIBUTE_HANDLER = TGChooserDialogHandler.class.getName();
	public static final String ATTRIBUTE_OPTIONS = "options";
	public static final String ATTRIBUTE_TITLE = "title";

	public TGChooserDialogController() {
		super();
	}

	@Override
	public TGChooserDialog<T> createNewInstance() {
		return new TGChooserDialog<T>();
	}
}
