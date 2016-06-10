package org.herac.tuxguitar.app.view.toolbar;

public class TGToolBarSectionDivider implements TGToolBarSection {
	
	public void createSection(final TGToolBar toolBar) {
		toolBar.getControl().createSeparator();
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
