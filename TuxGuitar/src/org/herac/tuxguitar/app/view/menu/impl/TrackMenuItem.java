/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.menu.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TrackMenuItem extends TGMenuItem{
	private MenuItem trackMenuItem;
	private TrackMenu menu;
	
	public TrackMenuItem(Shell shell,Menu parent, int style) {
		this.trackMenuItem = new MenuItem(parent, style);
		this.menu = new TrackMenu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		this.menu.showItems();
		this.trackMenuItem.setMenu(this.menu.getMenu());
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
