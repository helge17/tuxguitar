package app.tuxguitar.ui.jfx.widget;

import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.Region;

import app.tuxguitar.ui.widget.UISeparator;

public class JFXSeparator extends JFXControl<Separator> implements UISeparator {

	public JFXSeparator(JFXContainer<? extends Region> parent, Orientation orientation) {
		super(new Separator(orientation), parent);
	}
}
