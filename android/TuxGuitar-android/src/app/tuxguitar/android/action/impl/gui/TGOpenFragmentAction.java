package app.tuxguitar.android.action.impl.gui;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.fragment.TGFragmentController;
import app.tuxguitar.util.TGContext;

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
