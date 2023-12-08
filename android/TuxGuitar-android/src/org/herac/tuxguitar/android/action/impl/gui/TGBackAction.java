package org.herac.tuxguitar.android.action.impl.gui;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.util.TGContext;

public class TGBackAction extends TGActionBase{
	
	public static final String NAME = "action.gui.go-back";
	
	public static final String ATTRIBUTE_ACTIVITY = TGActivity.class.getName();
	
	public TGBackAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		TGActivity tgActivity = (TGActivity) context.getAttribute(ATTRIBUTE_ACTIVITY);
		if(!tgActivity.getNavigationManager().callOpenPreviousFragment()) {
			new Thread(new Runnable() {
				public void run() {
					TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
					tgActionManager.execute(TGExitAction.NAME, context);
				}
			}).start();
		}
	}
}
