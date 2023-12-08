package org.herac.tuxguitar.android.action.impl.layout;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.util.TGContext;

public class TGSetLayoutScaleAction extends TGActionBase{
	
	public static final String NAME = "action.view.layout-set-scale";
	
	public static final String ATTRIBUTE_SCALE = "scale";
	
	public TGSetLayoutScaleAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		Float scale = ((Float) context.getAttribute(ATTRIBUTE_SCALE));
		
		TGSongViewController.getInstance(getContext()).scale(scale.floatValue());
	}
}
