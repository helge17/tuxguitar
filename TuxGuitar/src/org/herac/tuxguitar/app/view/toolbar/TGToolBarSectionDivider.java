package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.ToolItem;

public class TGToolBarSectionDivider extends SelectionAdapter implements TGToolBarSection {
	
	public void createSection(final TGToolBar toolBar) {
		new ToolItem(toolBar.getControl(), SWT.SEPARATOR);
	}
	
	public void loadProperties(TGToolBar toolBar){
		//Nothing to do
	}
	
	public void loadIcons(TGToolBar toolBar){
		//Nothing to do
	}
	
	public void updateItems(TGToolBar toolBar){
		//Nothing to do
	}
}
