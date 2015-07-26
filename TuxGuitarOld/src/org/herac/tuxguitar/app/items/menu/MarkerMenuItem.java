/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessor;
import org.herac.tuxguitar.app.action.impl.marker.AddMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.GoFirstMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.GoLastMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.GoNextMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.GoPreviousMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.ListMarkersAction;
import org.herac.tuxguitar.app.items.MenuItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MarkerMenuItem extends MenuItems{
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
		this.add.addSelectionListener(new TGActionProcessor(AddMarkerAction.NAME));
		
		//--LIST--
		this.list = new MenuItem(this.menu, SWT.PUSH);
		this.list.addSelectionListener(new TGActionProcessor(ListMarkersAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--FIRST--
		this.first = new MenuItem(this.menu, SWT.PUSH);
		this.first.addSelectionListener(new TGActionProcessor(GoFirstMarkerAction.NAME));
		
		//--PREVIOUS--
		this.previous = new MenuItem(this.menu, SWT.PUSH);
		this.previous.addSelectionListener(new TGActionProcessor(GoPreviousMarkerAction.NAME));
		
		//--PREVIOUS--
		this.next = new MenuItem(this.menu, SWT.PUSH);
		this.next.addSelectionListener(new TGActionProcessor(GoNextMarkerAction.NAME));
		
		//--LAST--
		this.last = new MenuItem(this.menu, SWT.PUSH);
		this.last.addSelectionListener(new TGActionProcessor(GoLastMarkerAction.NAME));
		
		this.markerMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		//Nothing to do
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.markerMenuItem, "marker", null);
		setMenuItemTextAndAccelerator(this.add, "marker.add", AddMarkerAction.NAME);
		setMenuItemTextAndAccelerator(this.list, "marker.list", ListMarkersAction.NAME);
		setMenuItemTextAndAccelerator(this.first, "marker.first", GoFirstMarkerAction.NAME);
		setMenuItemTextAndAccelerator(this.last, "marker.last", GoLastMarkerAction.NAME);
		setMenuItemTextAndAccelerator(this.previous, "marker.previous", GoPreviousMarkerAction.NAME);
		setMenuItemTextAndAccelerator(this.next, "marker.next", GoNextMarkerAction.NAME);
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
