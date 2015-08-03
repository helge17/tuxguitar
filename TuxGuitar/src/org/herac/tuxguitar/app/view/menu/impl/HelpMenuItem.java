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
import org.herac.tuxguitar.app.action.impl.help.TGOpenAboutDialogAction;
import org.herac.tuxguitar.app.action.impl.help.TGOpenDocumentationDialogAction;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HelpMenuItem extends TGMenuItem{
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
		this.doc.addSelectionListener(this.createActionProcessor(TGOpenDocumentationDialogAction.NAME));
		
		//--ABOUT
		this.about = new MenuItem(this.menu, SWT.PUSH);
		this.about.addSelectionListener(this.createActionProcessor(TGOpenAboutDialogAction.NAME));
		
		this.helpMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.helpMenuItem, "help", null);
		setMenuItemTextAndAccelerator(this.doc, "help.doc", TGOpenDocumentationDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.about, "help.about", TGOpenAboutDialogAction.NAME);
	}
	
	public void loadIcons(){
		//Nothing to do
	}
	
	public void update(){
		//Nothing to do
	}
}
