package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.util.TGContext;

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
