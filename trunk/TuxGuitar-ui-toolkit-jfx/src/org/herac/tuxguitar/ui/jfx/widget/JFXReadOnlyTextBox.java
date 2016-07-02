package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.widget.UIReadOnlyTextBox;

public class JFXReadOnlyTextBox extends JFXTextControl<TextArea> implements UIReadOnlyTextBox {
	
	private static final float DEFAULT_WIDTH = 100f;
	private static final float DEFAULT_HEIGHT = 50f;
	
	public JFXReadOnlyTextBox(JFXContainer<? extends Region> parent, boolean vScroll, boolean hScroll) {
		super(new TextArea(), parent);
		
		this.getControl().setEditable(false);
	}
	
	public void computePackedSize() {
		if( this.getControl().getPrefWidth() == Region.USE_COMPUTED_SIZE ) {
			this.getControl().setPrefWidth(DEFAULT_WIDTH);
		}
		if( this.getControl().getPrefHeight() == Region.USE_COMPUTED_SIZE ) {
			this.getControl().setPrefHeight(DEFAULT_HEIGHT);
		}
		
		super.computePackedSize();
	}
}

