package org.herac.tuxguitar.android.action.impl.layout;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.util.TGContext;

public class TGSetChordNameEnabledAction extends TGActionBase{
	
	public static final String NAME = "action.view.layout-set-chord-name-enabled";
	
	public static final String ATTRIBUTE_SCALE = "scale";
	
	public TGSetChordNameEnabledAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		TGLayout tgLayout = TGSongViewController.getInstance(getContext()).getLayout();
		tgLayout.setStyle((tgLayout.getStyle() ^ TGLayout.DISPLAY_CHORD_NAME));
	}
}
