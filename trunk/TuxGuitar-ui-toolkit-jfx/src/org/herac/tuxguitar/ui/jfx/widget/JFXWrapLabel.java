package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.widget.UIWrapLabel;

public class JFXWrapLabel extends JFXLabel implements UIWrapLabel {
	
	private Float wrapWidth;
	
	public JFXWrapLabel(JFXContainer<? extends Region> parent) {
		super(parent);
	}

	public Float getWrapWidth() {
		return wrapWidth;
	}

	public void setWrapWidth(Float wrapWidth) {
		this.wrapWidth = wrapWidth;
	}
}
