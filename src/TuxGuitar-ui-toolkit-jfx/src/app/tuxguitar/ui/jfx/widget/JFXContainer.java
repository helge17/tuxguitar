package app.tuxguitar.ui.jfx.widget;

import javafx.scene.Node;
import javafx.scene.layout.Region;

import app.tuxguitar.ui.widget.UIContainer;

public interface JFXContainer<T extends Region> extends UIContainer {

	void addChild(JFXNode<? extends Node> uiControl);

	void removeChild(JFXNode<? extends Node> uiControl);
}
