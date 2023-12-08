package org.herac.tuxguitar.ui.swt.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class SWTMenuSubMenuItem extends SWTMenuItem implements UIMenuSubMenuItem {
	
	private SWTMenu subMenu;
	
	public SWTMenuSubMenuItem(MenuItem item, SWTMenu parent) {
		super(item, parent);
		
		this.subMenu = new SWTMenu(this.getParent().getControl().getShell(), SWT.DROP_DOWN);
		this.getControl().setMenu(this.subMenu.getControl());
	}

	public UIMenu getMenu() {
		return this.subMenu;
	}
}
