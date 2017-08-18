package org.herac.tuxguitar.ui.jfx.widget;

import org.herac.tuxguitar.ui.widget.UILegendPanel;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class JFXLegendPanel extends JFXLayoutContainer<TitledPane> implements UILegendPanel {
	
	private static final float PADDING_TOP = 26f;
	
	public JFXLegendPanel(JFXContainer<? extends Region> parent) {
		super(new TitledPane(), parent);
		
		this.getControl().setContent(new Pane());
		this.getControl().setCollapsible(false);
		this.getControl().setAnimated(false);
		this.getContentPane().setFocusTraversable(false);
	}

	public void addChild(JFXNode<? extends Node> uiControl) {
		super.addChild(uiControl);
		
		this.getContentPane().getChildren().add(uiControl.getControl());
	}
	
	public void removeChild(JFXNode<? extends Node> uiControl) {
		super.removeChild(uiControl);
		
		this.getContentPane().getChildren().remove(uiControl.getControl());
	}
	
	public Insets getPadding() {
		Insets padding = super.getPadding();
		
		return new Insets((padding.getTop() + PADDING_TOP), padding.getRight(), padding.getBottom(), padding.getLeft());
	}
	
	public Pane getContentPane() {
		return (Pane) this.getControl().getContent();
	}
	
	public String getText() {
		return this.getControl().getText();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}
	
	public void setFocus() {
		this.getContentPane().setFocusTraversable(true);
		this.getContentPane().requestFocus();
	}
}
