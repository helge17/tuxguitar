/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.layout;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetScoreEnabledAction extends Action{
	public static final String NAME = "action.view.layout-set-score-enabled";
	
	public SetScoreEnabledAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		ViewLayout layout = getEditor().getTablature().getViewLayout();
		layout.setStyle( ( layout.getStyle() ^ ViewLayout.DISPLAY_SCORE ) );
		if((layout.getStyle() & ViewLayout.DISPLAY_TABLATURE) == 0 && (layout.getStyle() & ViewLayout.DISPLAY_SCORE) == 0 ){
			layout.setStyle( ( layout.getStyle() ^ ViewLayout.DISPLAY_TABLATURE) );
		}
		updateTablature();
		return 0;
	}
}
