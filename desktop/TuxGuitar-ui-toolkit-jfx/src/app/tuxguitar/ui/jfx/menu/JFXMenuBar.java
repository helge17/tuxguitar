package app.tuxguitar.ui.jfx.menu;

import app.tuxguitar.ui.jfx.widget.JFXWindow;
import app.tuxguitar.ui.menu.UIMenuBar;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class JFXMenuBar extends JFXAbstractMenu<MenuBar> implements UIMenuBar {

	public JFXMenuBar(JFXWindow window) {
		super(new MenuBar());

		// Move menu to the system menu bar, mainly on macOS
		super.getControl().setUseSystemMenuBar(true);

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
