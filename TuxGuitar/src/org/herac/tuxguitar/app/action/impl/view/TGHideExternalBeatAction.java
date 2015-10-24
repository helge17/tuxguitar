package org.herac.tuxguitar.app.action.impl.view;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.editor.TGExternalBeatViewerManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGHideExternalBeatAction extends TGActionBase {

	public static final String NAME = "action.gui.hide-external-beat";
	
	public TGHideExternalBeatAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(final TGActionContext context) {
		TGExternalBeatViewerManager.getInstance(getContext()).hideExternalBeat(context);
	}
}
