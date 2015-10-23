package org.herac.tuxguitar.android.action.impl.gui;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.fragment.TGFragmentController;
import org.herac.tuxguitar.util.TGContext;

public class TGOpenFragmentAction extends TGActionBase{
	
	public static final String NAME = "action.gui.open-fragment";
	
	public static final String ATTRIBUTE_ACTIVITY = TGActivity.class.getName();
	public static final String ATTRIBUTE_CONTROLLER = TGFragmentController.class.getName();
	public static final String ATTRIBUTE_TAG_ID = "tagId";
	
	public TGOpenFragmentAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		String tagId = context.getAttribute(ATTRIBUTE_TAG_ID);
		TGFragmentController<?> tgFragmentController = context.getAttribute(ATTRIBUTE_CONTROLLER);
		
		TGActivity tgActivity = (TGActivity) context.getAttribute(ATTRIBUTE_ACTIVITY);
		tgActivity.getNavigationManager().processLoadFragment(tgFragmentController, tagId);
	}
}
