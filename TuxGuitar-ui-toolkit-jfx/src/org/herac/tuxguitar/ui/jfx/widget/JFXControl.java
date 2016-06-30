package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.jfx.menu.JFXPopupMenu;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;

public class JFXControl<T extends Control> extends JFXRegion<T> {
	
	public JFXControl(T control, JFXContainer<? extends Region> parent) {
		super(control, parent);
	}
	
	public void setToolTipText(String text) {
		super.setToolTipText(text);
		
		this.getControl().setTooltip(new Tooltip(text));
	}
	
	public void setPopupMenu(UIPopupMenu popupMenu) {
		super.setPopupMenu(popupMenu);
		
		this.getControl().setContextMenu(popupMenu != null ? ((JFXPopupMenu) popupMenu).getControl() : null);
	}
}
