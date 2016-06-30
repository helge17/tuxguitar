package org.herac.tuxguitar.ui.jfx.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class JFXMenu extends JFXAbstractMenu<Menu> {
	
	public JFXMenu(Menu control) {
		super(control);
	}
	
	public void addItem(JFXMenuItem<? extends MenuItem> item) {
		super.addItem(item);
		
		this.getControl().getItems().add(item.getControl());
	}
	
	public void removeItem(JFXMenuItem<? extends MenuItem> item) {
		this.getControl().getItems().remove(item.getControl());
		
		super.removeItem(item);
	}
}
