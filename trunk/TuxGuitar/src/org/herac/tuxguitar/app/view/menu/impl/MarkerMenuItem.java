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
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MarkerMenuItem extends TGMenuItem{
	private MenuItem markerMenuItem;
	private Menu menu;
	private MenuItem add;
	private MenuItem list;
	private MenuItem first;
	private MenuItem last;
	private MenuItem next;
	private MenuItem previous;
	
	public MarkerMenuItem(Shell shell,Menu parent, int style) {
		this.markerMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		//--ADD--
		this.add = new MenuItem(this.menu, SWT.PUSH);
		this.add.addSelectionListener(this.createActionProcessor(TGOpenMarkerEditorAction.NAME));
		
		//--LIST--
		this.list = new MenuItem(this.menu, SWT.PUSH);
		this.list.addSelectionListener(this.createActionProcessor(TGToggleMarkerListAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--FIRST--
		this.first = new MenuItem(this.menu, SWT.PUSH);
		this.first.addSelectionListener(this.createActionProcessor(TGGoFirstMarkerAction.NAME));
		
		//--PREVIOUS--
		this.previous = new MenuItem(this.menu, SWT.PUSH);
		this.previous.addSelectionListener(this.createActionProcessor(TGGoPreviousMarkerAction.NAME));
		
		//--PREVIOUS--
		this.next = new MenuItem(this.menu, SWT.PUSH);
		this.next.addSelectionListener(this.createActionProcessor(TGGoNextMarkerAction.NAME));
		
		//--LAST--
		this.last = new MenuItem(this.menu, SWT.PUSH);
		this.last.addSelectionListener(this.createActionProcessor(TGGoLastMarkerAction.NAME));
		
		this.markerMenuItem.setMenu(this.menu);
		
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
