package org.herac.tuxguitar.android.action.impl.layout;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.util.TGContext;

public class TGSetScoreEnabledAction extends TGActionBase{
	
	public static final String NAME = "action.view.layout-set-score-enabled";
	
	public static final String ATTRIBUTE_SCALE = "scale";
	
	public TGSetScoreEnabledAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		TGLayout tgLayout = TGSongViewController.getInstance(getContext()).getLayout();
		tgLayout.setStyle((tgLayout.getStyle() ^ TGLayout.DISPLAY_SCORE));
		if((tgLayout.getStyle() & TGLayout.DISPLAY_TABLATURE) == 0 && (tgLayout.getStyle() & TGLayout.DISPLAY_SCORE) == 0 ){
			tgLayout.setStyle( ( tgLayout.getStyle() ^ TGLayout.DISPLAY_TABLATURE) );
		}
	}
}
