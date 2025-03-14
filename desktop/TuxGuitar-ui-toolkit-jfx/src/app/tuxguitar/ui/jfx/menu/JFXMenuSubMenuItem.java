package app.tuxguitar.ui.jfx.menu;

import javafx.scene.control.Menu;

import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;

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
