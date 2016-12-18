package org.herac.tuxguitar.ui.jfx.widget;

import org.herac.tuxguitar.ui.widget.UISlider;

import javafx.geometry.Orientation;
import javafx.scene.layout.Region;

public class JFXSlider extends JFXAbstractScrollBar implements UISlider {
	
	public JFXSlider(JFXContainer<? extends Region> parent, Orientation orientation) {
		super(parent, orientation);
	}
}
