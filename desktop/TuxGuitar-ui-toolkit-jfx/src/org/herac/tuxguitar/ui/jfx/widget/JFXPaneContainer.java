package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public abstract class JFXPaneContainer<T extends Pane> extends JFXLayoutContainer<T> {
	
	public JFXPaneContainer(T control, JFXContainer<? extends Region> parent) {
		super(control, parent);
	}
	
	public void addChild(JFXNode<? extends Node> uiControl) {
		super.addChild(uiControl);
		
		this.getControl().getChildren().add(uiControl.getControl());
	}
	
	public void removeChild(JFXNode<? extends Node> uiControl) {
		super.removeChild(uiControl);
		
		this.getControl().getChildren().remove(uiControl.getControl());
	}
}
