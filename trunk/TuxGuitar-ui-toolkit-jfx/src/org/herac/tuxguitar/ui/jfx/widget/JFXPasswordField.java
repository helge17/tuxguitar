package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.PasswordField;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.widget.UIPasswordField;

public class JFXPasswordField extends JFXEditableTextControl<PasswordField> implements UIPasswordField {
	
	public JFXPasswordField(JFXContainer<? extends Region> parent) {
		super(new PasswordField(), parent);
	}
}
