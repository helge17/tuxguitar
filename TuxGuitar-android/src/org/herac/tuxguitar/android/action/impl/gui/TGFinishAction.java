package org.herac.tuxguitar.android.action.impl.gui;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.util.TGContext;

public class TGFinishAction extends TGActionBase{
	
	public static final String NAME = "action.gui.finish";
	
	public static final String ATTRIBUTE_ACTIVITY = TGActivity.class.getName();
	
	public TGFinishAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		TGActivity tgActivity = (TGActivity) context.getAttribute(ATTRIBUTE_ACTIVITY);
		tgActivity.finish();
	}
}
