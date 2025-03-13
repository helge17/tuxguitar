package app.tuxguitar.ui.jfx.widget;

import javafx.scene.control.PasswordField;
import javafx.scene.layout.Region;

import app.tuxguitar.ui.widget.UIPasswordField;

public class JFXPasswordField extends JFXEditableTextControl<PasswordField> implements UIPasswordField {

	public JFXPasswordField(JFXContainer<? extends Region> parent) {
		super(new PasswordField(), parent);
	}
}
