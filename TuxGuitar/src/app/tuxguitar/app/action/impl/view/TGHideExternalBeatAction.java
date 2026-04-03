package app.tuxguitar.app.action.impl.view;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.editor.TGExternalBeatViewerManager;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGHideExternalBeatAction extends TGActionBase {

	public static final String NAME = "action.gui.hide-external-beat";

	public TGHideExternalBeatAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		TGExternalBeatViewerManager.getInstance(getContext()).hideExternalBeat(context);
	}
}
