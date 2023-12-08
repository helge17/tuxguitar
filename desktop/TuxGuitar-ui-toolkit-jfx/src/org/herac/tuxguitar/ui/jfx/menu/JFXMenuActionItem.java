package org.herac.tuxguitar.ui.jfx.menu;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerManager;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;

public class JFXMenuActionItem extends JFXMenuItem<MenuItem> implements UIMenuActionItem {
	
	private JFXSelectionListenerManager<ActionEvent> selectionListener;
	
	public JFXMenuActionItem(MenuItem item, JFXMenuItemContainer parent) {
		super(item, parent);
		
		this.selectionListener = new JFXSelectionListenerManager<ActionEvent>(this);
	}
	
	public JFXMenuActionItem(JFXMenuItemContainer parent) {
		this(new MenuItem(), parent);
	}
	
	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().setOnAction(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().setOnAction(null);
		}
	}
}
