package org.herac.tuxguitar.app.view.toolbar.main;

public class TGMainToolBarSectionDivider extends TGMainToolBarSection {
	
	public TGMainToolBarSectionDivider(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.getToolBar().getControl().createSeparator();
	}
	
	public void loadProperties(){
		//Nothing to do
	}
	
	public void loadIcons(){
		//Nothing to do
	}
	
	public void updateItems(){
		//Nothing to do
	}
}
