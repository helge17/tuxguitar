package org.herac.tuxguitar.ui.jfx.toolbar;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerManager;
import org.herac.tuxguitar.ui.jfx.menu.JFXPopupMenu;
import org.herac.tuxguitar.ui.jfx.widget.JFXButton;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIPosition;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.toolbar.UIToolActionMenuItem;

public class JFXToolActionMenuItem extends JFXButton implements UIToolActionMenuItem {
	
	private Double mouseX;
	private UIPopupMenu menu;
	private JFXSelectionListenerManager<ActionEvent> selectionListener;
	
	public JFXToolActionMenuItem(JFXToolBar parent) {
		super(parent);
		
		this.menu = new JFXPopupMenu(parent.getControl().getScene().getWindow());
		this.selectionListener = new JFXSelectionListenerManager<ActionEvent>(this);
		
		this.getControl().setFocusTraversable(false);
		this.getControl().setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				JFXToolActionMenuItem.this.handleMouseEvent(event);
			}
		});
		this.getControl().setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				JFXToolActionMenuItem.this.handleActionEvent(event);
			}
		});
	}

	public void addSelectionListener(UISelectionListener listener) {
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
	}
	
	public UIMenu getMenu() {
		return this.menu;
	}
	
	public void openMenu() {
        Bounds bounds = this.getControl().getBoundsInLocal();
        Bounds screenBounds = this.getControl().localToScreen(bounds);
        
		this.menu.open(new UIPosition((float) screenBounds.getMinX(), (float) (screenBounds.getMinY() + bounds.getHeight())));
	}
	
	public void handleActionEvent(ActionEvent event) {
		UIRectangle bounds = this.getBounds();
		if( this.mouseX == null || this.mouseX < (bounds.getWidth() - 10)) {
			this.selectionListener.handle(event);
		} else {
			this.openMenu();
		}
	}
	
	public void handleMouseEvent(MouseEvent event) {
		this.mouseX = event.getX();
	}
}
