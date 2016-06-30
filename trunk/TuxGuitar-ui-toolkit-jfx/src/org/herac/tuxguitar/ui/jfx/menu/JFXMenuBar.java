package org.herac.tuxguitar.ui.jfx.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import org.herac.tuxguitar.ui.menu.UIMenuBar;

public class JFXMenuBar extends JFXAbstractMenu<MenuBar> implements UIMenuBar {
	
	public JFXMenuBar() {
		super(new MenuBar());
	}
	
	public void addItem(JFXMenuItem<? extends MenuItem> item) {
		super.addItem(item);
		
		if( item.getControl() instanceof Menu ) {
			this.getControl().getMenus().add((Menu) item.getControl());
		}
	}
	
	public void removeItem(JFXMenuItem<? extends MenuItem> item) {
		if( item.getControl() instanceof Menu ) {
			this.getControl().getMenus().remove((Menu) item.getControl());
		}
		
		super.removeItem(item);
	}
}
