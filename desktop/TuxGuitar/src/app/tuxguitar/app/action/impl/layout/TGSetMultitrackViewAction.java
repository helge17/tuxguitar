package app.tuxguitar.app.action.impl.layout;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.util.TGContext;

public class TGSetMultitrackViewAction extends TGActionBase{

	public static final String NAME = "action.view.layout-set-multitrack";

	public TGSetMultitrackViewAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGLayout layout = TablatureEditor.getInstance(getContext()).getTablature().getViewLayout();
		layout.setStyle( ( layout.getStyle() ^ TGLayout.DISPLAY_MULTITRACK ) );
		if ((layout.getStyle() & TGLayout.DISPLAY_MULTITRACK) != 0) {
			new TGActionProcessor(getContext(), TGSetLinearLayoutAction.NAME).process();
		} else {
			new TGActionProcessor(getContext(), TGSetPageLayoutAction.NAME).process();
		}
	}
}
