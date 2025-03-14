package app.tuxguitar.android.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.util.TGContext;

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
