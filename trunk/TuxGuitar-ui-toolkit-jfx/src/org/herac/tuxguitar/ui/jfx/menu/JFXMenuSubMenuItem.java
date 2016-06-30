package org.herac.tuxguitar.ui.jfx.menu;

import javafx.scene.control.Menu;

import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class JFXMenuSubMenuItem extends JFXMenuItem<Menu> implements UIMenuSubMenuItem {
	
	private JFXMenu subMenu;
	
	public JFXMenuSubMenuItem(JFXMenuItemContainer parent) {
		super(new Menu(), parent);
		
		this.subMenu = new JFXMenu(this.getControl());
	}

	public UIMenu getMenu() {
		return this.subMenu;
	}
}
