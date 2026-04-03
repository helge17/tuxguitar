package app.tuxguitar.app.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.util.TGContext;

public class TGSetChordDiagramEnabledAction extends TGActionBase{

	public static final String NAME = "action.view.layout-set-chord-diagram-enabled";

	public TGSetChordDiagramEnabledAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGLayout layout = TablatureEditor.getInstance(getContext()).getTablature().getViewLayout();
		layout.setStyle((layout.getStyle() ^ TGLayout.DISPLAY_CHORD_DIAGRAM));
	}
}
