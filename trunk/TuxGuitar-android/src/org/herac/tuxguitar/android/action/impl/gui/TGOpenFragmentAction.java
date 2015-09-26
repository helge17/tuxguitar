package org.herac.tuxguitar.android.action.impl.gui;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.fragment.TGFragment;
import org.herac.tuxguitar.util.TGContext;

public class TGOpenFragmentAction extends TGActionBase{
	
	public static final String NAME = "action.gui.open-fragment";
	
	public static final String ATTRIBUTE_ACTIVITY = TGActivity.class.getName();
	public static final String ATTRIBUTE_FRAGMENT = TGFragment.class.getName();
	public static final String ATTRIBUTE_TAG_ID = "tagId";
	
	public TGOpenFragmentAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		String tagId = (String) context.getAttribute(ATTRIBUTE_TAG_ID);
		TGFragment tgFragment = (TGFragment) context.getAttribute(ATTRIBUTE_FRAGMENT);
		
		TGActivity tgActivity = (TGActivity) context.getAttribute(ATTRIBUTE_ACTIVITY);
		tgActivity.getNavigationManager().processLoadFragment(tgFragment, tagId);
	}
}
