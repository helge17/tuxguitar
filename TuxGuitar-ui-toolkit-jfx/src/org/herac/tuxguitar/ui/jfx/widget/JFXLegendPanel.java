package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.widget.UILegendPanel;

public class JFXLegendPanel extends JFXPanel implements UILegendPanel {
	
	private String text;
	
	public JFXLegendPanel(JFXContainer<? extends Region> parent) {
		super(parent, true);
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
