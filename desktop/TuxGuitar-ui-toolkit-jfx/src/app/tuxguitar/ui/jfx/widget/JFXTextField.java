package app.tuxguitar.ui.jfx.widget;

import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import app.tuxguitar.ui.widget.UITextField;

public class JFXTextField extends JFXEditableTextControl<TextField> implements UITextField {

	public JFXTextField(JFXContainer<? extends Region> parent) {
		super(new TextField(), parent);
	}
}
