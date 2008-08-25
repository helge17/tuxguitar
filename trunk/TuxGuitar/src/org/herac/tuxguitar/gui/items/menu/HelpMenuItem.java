/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.help.ShowAboutAction;
import org.herac.tuxguitar.gui.actions.help.ShowDocAction;
import org.herac.tuxguitar.gui.items.MenuItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HelpMenuItem extends MenuItems{
	private MenuItem helpMenuItem;
	private Menu menu; 
	private MenuItem doc;
	private MenuItem about;
	
	public HelpMenuItem(Shell shell,Menu parent, int style) {
		this.helpMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		//--Doc
		this.doc = new MenuItem(this.menu, SWT.PUSH);
		this.doc.addSelectionListener(TuxGuitar.instance().getAction(ShowDocAction.NAME));
		
		//--ABOUT
		this.about = new MenuItem(this.menu, SWT.PUSH);
		this.about.addSelectionListener(TuxGuitar.instance().getAction(ShowAboutAction.NAME));
		
		this.helpMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.helpMenuItem, "help", null);
		setMenuItemTextAndAccelerator(this.doc, "help.doc", ShowDocAction.NAME);
		setMenuItemTextAndAccelerator(this.about, "help.about", ShowAboutAction.NAME);
	}
	
	public void loadIcons(){
		//Nothing to do
	}
	
	public void update(){
		//Nothing to do
	}
}
