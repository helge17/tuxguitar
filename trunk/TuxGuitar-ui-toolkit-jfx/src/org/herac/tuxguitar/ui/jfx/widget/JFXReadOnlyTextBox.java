package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.widget.UIReadOnlyTextBox;

public class JFXReadOnlyTextBox extends JFXTextControl<TextArea> implements UIReadOnlyTextBox {
	
	public JFXReadOnlyTextBox(JFXContainer<? extends Region> parent, boolean vScroll, boolean hScroll) {
		super(new TextArea(), parent);
		
		this.getControl().setEditable(false);
	}
}

