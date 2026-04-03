package app.tuxguitar.ui.jfx.menu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;

import app.tuxguitar.ui.event.UIMenuHideListener;
import app.tuxguitar.ui.event.UIMenuShowListener;
import app.tuxguitar.ui.jfx.event.JFXMenuHideListenerManager;
import app.tuxguitar.ui.jfx.event.JFXMenuShowListenerManager;
import app.tuxguitar.ui.jfx.widget.JFXWindow;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.resource.UIPosition;

public class JFXPopupMenu extends JFXAbstractMenu<ContextMenu> implements UIPopupMenu {

	private Window window;
	private JFXMenuShowListenerManager menuShowListener;
	private JFXMenuHideListenerManager menuHideListener;

	public JFXPopupMenu(Window window) {
		super(new ContextMenu());

		this.window = window;
		this.menuShowListener = new JFXMenuShowListenerManager(this);
		this.menuHideListener = new JFXMenuHideListenerManager(this);
	}

	public JFXPopupMenu(JFXWindow window) {
		this(window.getStage());
	}

	public void addItem(JFXMenuItem<? extends MenuItem> item) {
		super.addItem(item);

		this.getControl().getItems().add(item.getControl());
	}

	public void removeItem(JFXMenuItem<? extends MenuItem> item) {
		this.getControl().getItems().remove(item.getControl());

		super.removeItem(item);
	}

	public void open(UIPosition position) {
		this.getControl().show(this.window, position.getX(), position.getY());
	}

	public void addMenuShowListener(UIMenuShowListener listener) {
		if( this.menuShowListener.isEmpty() ) {
			this.getControl().setOnShown(this.menuShowListener);
		}
		this.menuShowListener.addListener(listener);
	}

	public void addMenuHideListener(UIMenuHideListener listener) {
		if( this.menuHideListener.isEmpty() ) {
			this.getControl().setOnHidden(this.menuHideListener);
		}
		this.menuHideListener.addListener(listener);
	}

	public void removeMenuShowListener(UIMenuShowListener listener) {
		this.menuShowListener.removeListener(listener);
		if( this.menuShowListener.isEmpty() ) {
			this.getControl().setOnShown(null);
		}
	}

	public void removeMenuHideListener(UIMenuHideListener listener) {
		this.menuHideListener.removeListener(listener);
		if( this.menuHideListener.isEmpty() ) {
			this.getControl().setOnHidden(null);
		}
	}
}
