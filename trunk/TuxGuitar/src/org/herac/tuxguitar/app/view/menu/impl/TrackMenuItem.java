package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class TrackMenuItem extends TGMenuItem {
	
	private UIMenuSubMenuItem trackMenuItem;
	private TrackMenu menu;
	
	public TrackMenuItem(UIMenu parent) {
		this.trackMenuItem = parent.createSubMenuItem();
		this.menu = new TrackMenu(this.trackMenuItem.getMenu());
	}
	
	public void showItems(){
		this.menu.showItems();
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.setMenuItemTextAndAccelerator(this.trackMenuItem, "track", null);
		this.menu.loadProperties();
	}
	
	public void update(){
		this.menu.update();
	}
	
	public void loadIcons(){
		//Nothing to do
	}
}
