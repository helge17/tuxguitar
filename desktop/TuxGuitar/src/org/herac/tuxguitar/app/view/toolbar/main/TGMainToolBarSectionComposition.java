package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;

public class TGMainToolBarSectionComposition extends TGMainToolBarSection {
	
	private UIToolActionItem properties;
	
	public TGMainToolBarSectionComposition(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.properties = this.getToolBar().getControl().createActionItem();
		this.properties.addSelectionListener(this.createActionProcessor(TGOpenSongInfoDialogAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties() {
		this.properties.setToolTipText(this.getText("composition.properties"));
	}
	
	public void loadIcons() {
		this.properties.setImage(this.getIconManager().getSongProperties());
	}
	
	public void updateItems() {
		//Nothing to do
	}
}
