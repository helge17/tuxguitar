package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.Node;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.widget.UIContainer;

public interface JFXContainer<T extends Region> extends UIContainer {
	
	T getControl();
	
	void addChild(JFXNode<? extends Node> uiControl);
	
	void removeChild(JFXNode<? extends Node> uiControl);
}
