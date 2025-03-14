package app.tuxguitar.app.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.util.TGContext;

public class SetChordNameEnabledAction extends TGActionBase {

	public static final String NAME = "action.view.layout-set-chord-name-enabled";

	public SetChordNameEnabledAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGLayout layout = TuxGuitar.instance().getTablatureEditor().getTablature().getViewLayout();
		layout.setStyle( ( layout.getStyle() ^ TGLayout.DISPLAY_CHORD_NAME ) );
	}
}
