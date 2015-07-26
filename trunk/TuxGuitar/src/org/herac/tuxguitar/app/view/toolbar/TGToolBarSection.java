package org.herac.tuxguitar.app.view.toolbar;

public interface TGToolBarSection {
	
	void createSection(TGToolBar toolBar);
	
	void updateItems(TGToolBar toolBar);
	
	void loadIcons(TGToolBar toolBar);
	
	void loadProperties(TGToolBar toolBar);
}
