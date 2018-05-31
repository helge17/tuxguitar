package org.herac.tuxguitar.android.action.impl.view;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.util.TGContext;

public class TGShowSmartMenuAction extends TGActionBase{

	public static final String NAME = "action.view.show-smart-menu";

	public TGShowSmartMenuAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		TGSongViewController.getInstance(getContext()).getSmartMenu().openSmartMenu(context);
	}
}
