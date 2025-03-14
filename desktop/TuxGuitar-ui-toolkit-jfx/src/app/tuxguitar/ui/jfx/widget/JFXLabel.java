package app.tuxguitar.ui.jfx.widget;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import app.tuxguitar.ui.widget.UILabel;

public class JFXLabel extends JFXLabeled<Label> implements UILabel {

	public JFXLabel(JFXContainer<? extends Region> parent) {
		super(new Label(), parent);
	}
}
