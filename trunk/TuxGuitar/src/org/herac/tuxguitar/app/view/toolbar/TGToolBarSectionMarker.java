package org.herac.tuxguitar.app.view.toolbar;

import org.herac.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import org.herac.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;

public class TGToolBarSectionMarker implements TGToolBarSection {
	
	private UIToolMenuItem menuItem;
	
	private UIMenuActionItem add;
	private UIMenuActionItem list;
	private UIMenuActionItem first;
	private UIMenuActionItem previous;
	private UIMenuActionItem next;
	private UIMenuActionItem last;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = toolBar.getControl().createMenuItem();
		
		//--ADD--
		this.add = this.menuItem.getMenu().createActionItem();
		this.add.addSelectionListener(toolBar.createActionProcessor(TGOpenMarkerEditorAction.NAME));
		
		//--LIST--
		this.list = this.menuItem.getMenu().createActionItem();
		this.list.addSelectionListener(toolBar.createActionProcessor(TGToggleMarkerListAction.NAME));
		
		//--SEPARATOR--
		this.menuItem.getMenu().createSeparator();
		
		//--FIRST--
		this.first = this.menuItem.getMenu().createActionItem();
		this.first.addSelectionListener(toolBar.createActionProcessor(TGGoFirstMarkerAction.NAME));
		
		//--PREVIOUS--
		this.previous = this.menuItem.getMenu().createActionItem();
		this.previous.addSelectionListener(toolBar.createActionProcessor(TGGoPreviousMarkerAction.NAME));
		
		//--PREVIOUS--
		this.next = this.menuItem.getMenu().createActionItem();
		this.next.addSelectionListener(toolBar.createActionProcessor(TGGoNextMarkerAction.NAME));
		
		//--LAST--
		this.last = this.menuItem.getMenu().createActionItem();
		this.last.addSelectionListener(toolBar.createActionProcessor(TGGoLastMarkerAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.menuItem.setToolTipText(toolBar.getText("marker"));
		this.add.setText(toolBar.getText("marker.add"));
		this.list.setText(toolBar.getText("marker.list"));
		this.first.setText(toolBar.getText("marker.first"));
		this.previous.setText(toolBar.getText("marker.previous"));
		this.next.setText(toolBar.getText("marker.next"));
		this.last.setText(toolBar.getText("marker.last"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getMarkerList());
		this.add.setImage(toolBar.getIconManager().getMarkerAdd());
		this.list.setImage(toolBar.getIconManager().getMarkerList());
		this.first.setImage(toolBar.getIconManager().getMarkerFirst());
		this.previous.setImage(toolBar.getIconManager().getMarkerPrevious());
		this.next.setImage(toolBar.getIconManager().getMarkerNext());
		this.last.setImage(toolBar.getIconManager().getMarkerLast());
	}
	
	public void updateItems(TGToolBar toolBar){
		//Nothing to do
	}
}
