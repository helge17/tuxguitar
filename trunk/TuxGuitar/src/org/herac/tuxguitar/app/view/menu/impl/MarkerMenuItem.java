package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import org.herac.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class MarkerMenuItem extends TGMenuItem {
	
	private UIMenuSubMenuItem markerMenuItem;
	private UIMenuActionItem add;
	private UIMenuActionItem list;
	private UIMenuActionItem first;
	private UIMenuActionItem last;
	private UIMenuActionItem next;
	private UIMenuActionItem previous;
	
	public MarkerMenuItem(UIMenu parent) {
		this.markerMenuItem = parent.createSubMenuItem();
	}
	
	public void showItems(){
		//--ADD--
		this.add = this.markerMenuItem.getMenu().createActionItem();
		this.add.addSelectionListener(this.createActionProcessor(TGOpenMarkerEditorAction.NAME));
		
		//--LIST--
		this.list = this.markerMenuItem.getMenu().createActionItem();
		this.list.addSelectionListener(this.createActionProcessor(TGToggleMarkerListAction.NAME));
		
		//--SEPARATOR--
		this.markerMenuItem.getMenu().createSeparator();
		
		//--FIRST--
		this.first = this.markerMenuItem.getMenu().createActionItem();
		this.first.addSelectionListener(this.createActionProcessor(TGGoFirstMarkerAction.NAME));
		
		//--PREVIOUS--
		this.previous = this.markerMenuItem.getMenu().createActionItem();
		this.previous.addSelectionListener(this.createActionProcessor(TGGoPreviousMarkerAction.NAME));
		
		//--NEXT--
		this.next = this.markerMenuItem.getMenu().createActionItem();
		this.next.addSelectionListener(this.createActionProcessor(TGGoNextMarkerAction.NAME));
		
		//--LAST--
		this.last = this.markerMenuItem.getMenu().createActionItem();
		this.last.addSelectionListener(this.createActionProcessor(TGGoLastMarkerAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		//Nothing to do
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.markerMenuItem, "marker", null);
		setMenuItemTextAndAccelerator(this.add, "marker.add", TGOpenMarkerEditorAction.NAME);
		setMenuItemTextAndAccelerator(this.list, "marker.list", TGToggleMarkerListAction.NAME);
		setMenuItemTextAndAccelerator(this.first, "marker.first", TGGoFirstMarkerAction.NAME);
		setMenuItemTextAndAccelerator(this.last, "marker.last", TGGoLastMarkerAction.NAME);
		setMenuItemTextAndAccelerator(this.previous, "marker.previous", TGGoPreviousMarkerAction.NAME);
		setMenuItemTextAndAccelerator(this.next, "marker.next", TGGoNextMarkerAction.NAME);
	}
	
	public void loadIcons(){
		this.add.setImage(TuxGuitar.getInstance().getIconManager().getMarkerAdd());
		this.list.setImage(TuxGuitar.getInstance().getIconManager().getMarkerList());
		this.first.setImage(TuxGuitar.getInstance().getIconManager().getMarkerFirst());
		this.previous.setImage(TuxGuitar.getInstance().getIconManager().getMarkerPrevious());
		this.next.setImage(TuxGuitar.getInstance().getIconManager().getMarkerNext());
		this.last.setImage(TuxGuitar.getInstance().getIconManager().getMarkerLast());
	}
}
