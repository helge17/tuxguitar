/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.layout;

import java.awt.AWTEvent;

import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.editors.tab.layout.ViewLayout;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetChordDiagramEnabledAction extends Action{
	public static final String NAME = "action.view.layout-set-chord-diagram-enabled";
	
	public SetChordDiagramEnabledAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(AWTEvent e){
		ViewLayout layout = getEditor().getTablature().getViewLayout();
		layout.setStyle( ( layout.getStyle() ^ ViewLayout.DISPLAY_CHORD_DIAGRAM) );
		updateTablature();
		return 0;
	}
}
