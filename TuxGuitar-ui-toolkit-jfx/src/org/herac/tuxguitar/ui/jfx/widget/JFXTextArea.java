package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.widget.UITextArea;

public class JFXTextArea extends JFXEditableTextControl<TextArea> implements UITextArea {
	
	public JFXTextArea(JFXContainer<? extends Region> parent, boolean vScroll, boolean hScroll) {
		super(new TextArea(), parent);
	}
}
