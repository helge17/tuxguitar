package app.tuxguitar.ui.jfx.toolbar;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;

import app.tuxguitar.ui.jfx.menu.JFXPopupMenu;
import app.tuxguitar.ui.jfx.widget.JFXButton;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.resource.UIPosition;
import app.tuxguitar.ui.toolbar.UIToolMenuItem;

public class JFXToolMenuItem extends JFXButton implements UIToolMenuItem {

	private UIPopupMenu menu;

	public JFXToolMenuItem(JFXToolBar parent) {
		super(parent);

		this.menu = new JFXPopupMenu(parent.getControl().getScene().getWindow());
		this.getControl().setFocusTraversable(false);
		this.getControl().setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				JFXToolMenuItem.this.openMenu();
			}
		});
	}

	public UIMenu getMenu() {
		return this.menu;
	}

	public void openMenu() {
        Bounds bounds = this.getControl().getBoundsInLocal();
        Bounds screenBounds = this.getControl().localToScreen(bounds);

		this.menu.open(new UIPosition((float) screenBounds.getMinX(), (float) (screenBounds.getMinY() + bounds.getHeight())));
	}
}
