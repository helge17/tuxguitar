package app.tuxguitar.android.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.util.TGContext;

public class TGToggleHighlightPlayedBeatAction extends TGActionBase {

	public static final String NAME = "action.transport.highlight-played-beat";

	public TGToggleHighlightPlayedBeatAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGLayout tgLayout = TGSongViewController.getInstance(getContext()).getLayout();
		tgLayout.setStyle((tgLayout.getStyle() ^ TGLayout.HIGHLIGHT_PLAYED_BEAT));
	}
}
