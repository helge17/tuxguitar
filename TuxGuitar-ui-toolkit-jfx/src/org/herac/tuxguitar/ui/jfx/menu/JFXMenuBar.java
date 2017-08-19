package org.herac.tuxguitar.ui.jfx.menu;

import org.herac.tuxguitar.ui.jfx.widget.JFXWindow;
import org.herac.tuxguitar.ui.menu.UIMenuBar;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class JFXMenuBar extends JFXAbstractMenu<MenuBar> implements UIMenuBar {
	
	public JFXMenuBar(JFXWindow window) {
		super(new MenuBar());
		
		window.setMenuBar(this);
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
