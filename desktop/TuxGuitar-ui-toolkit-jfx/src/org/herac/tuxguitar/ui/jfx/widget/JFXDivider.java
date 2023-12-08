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
	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		this.setPackedSize(new UISize(fixedWidth != null ? fixedWidth : DEFAULT_PACKED_SIZE, fixedHeight != null ? fixedHeight : DEFAULT_PACKED_SIZE));
	}
}
