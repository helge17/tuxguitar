package app.tuxguitar.ui.jfx.menu;

import javafx.scene.control.SeparatorMenuItem;

import app.tuxguitar.ui.UIComponent;

public class JFXMenuSeparatorItem extends JFXMenuItem<SeparatorMenuItem> implements UIComponent {

	public JFXMenuSeparatorItem(JFXMenuItemContainer parent) {
		super(new SeparatorMenuItem(), parent);
	}
}
