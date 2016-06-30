package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import org.herac.tuxguitar.ui.widget.UIPanel;

public class JFXPanel extends JFXLayoutContainer<Pane> implements UIPanel {
	
	public JFXPanel(JFXContainer<? extends Region> parent, boolean bordered) {
		super(new Pane(), parent);
		
		if( bordered ) {
			this.getControl().setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
		}
	}
}
