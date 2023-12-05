package org.herac.tuxguitar.app.action.impl.layout;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.util.TGContext;

public class SetScoreEnabledAction extends TGActionBase {
	
	public static final String NAME = "action.view.layout-set-score-enabled";
	
	public SetScoreEnabledAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGLayout layout = TuxGuitar.instance().getTablatureEditor().getTablature().getViewLayout();
		layout.setStyle( ( layout.getStyle() ^ TGLayout.DISPLAY_SCORE ) );
		if((layout.getStyle() & TGLayout.DISPLAY_TABLATURE) == 0 && (layout.getStyle() & TGLayout.DISPLAY_SCORE) == 0 ){
			layout.setStyle( ( layout.getStyle() ^ TGLayout.DISPLAY_TABLATURE) );
		}
	}
}
