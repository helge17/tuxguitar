package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;

public class JFXControl<T extends Control> extends JFXRegion<T> {
	
	public JFXControl(T control, JFXContainer<? extends Region> parent) {
		super(control, parent);
	}
	
	public void setToolTipText(String text) {
		super.setToolTipText(text);
		
		this.getControl().setTooltip(new Tooltip(text));
	}
}
