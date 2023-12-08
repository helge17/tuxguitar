package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.widget.UIReadOnlyTextField;

public class JFXReadOnlyTextField extends JFXTextControl<TextField> implements UIReadOnlyTextField {
	
	public JFXReadOnlyTextField(JFXContainer<? extends Region> parent) {
		super(new TextField(), parent);
		
		this.getControl().setEditable(false);
	}
}
