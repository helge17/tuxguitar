package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarSectionDivider extends TGMainToolBarSection {

	public TGMainToolBarSectionDivider(TGContext context, UIToolBar toolBar) {
		super(context, toolBar);
	}

	public void createSection() {
		this.getToolBar().createSeparator();
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
