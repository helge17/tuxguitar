package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIDivider;

public class JFXDivider extends JFXRegion<Pane> implements UIDivider {
	
	private static final float DEFAULT_PACKED_SIZE = 2f;
	
	public JFXDivider(JFXContainer<? extends Region> parent) {
		super(new Pane(), parent);
	}
	
	@Override
	public void computePackedSize() {
		this.setPackedSize(new UISize(DEFAULT_PACKED_SIZE, DEFAULT_PACKED_SIZE));
	}
}
