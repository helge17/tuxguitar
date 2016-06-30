package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UIResizeListener;
import org.herac.tuxguitar.ui.jfx.event.JFXResizeListenerManager;
import org.herac.tuxguitar.ui.jfx.property.JFXBackgroundProperty;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIRectangle;

public abstract class JFXRegion<T extends Region> extends JFXNode<T> {
	
	private JFXResizeListenerManager resizeListener;
	private JFXBackgroundProperty backgroundProperty;
	
	public JFXRegion(T control, JFXContainer<? extends Region> parent) {
		super(control, parent);
		
		this.resizeListener = new JFXResizeListenerManager(this);
		this.backgroundProperty = new JFXBackgroundProperty(this.getControl().backgroundProperty());
	}
	
	public UIRectangle getBounds() {
		UIRectangle bounds = new UIRectangle();
		bounds.getPosition().setX((float) this.getControl().getLayoutX());
		bounds.getPosition().setY((float) this.getControl().getLayoutY());
		bounds.getSize().setWidth((float) this.getControl().getWidth());
		bounds.getSize().setHeight((float) this.getControl().getHeight());
		
		return bounds;
	}
	
	public void setBounds(UIRectangle bounds) {
		this.getControl().setLayoutX(bounds.getX());
		this.getControl().setLayoutY(bounds.getY());
		this.getControl().resize(bounds.getWidth(), bounds.getHeight());
	}
	
	public UIColor getBgColor() {
		if( super.getBgColor() == null ) {
			super.setBgColor(this.backgroundProperty.get());
		}
		return super.getBgColor();
	}
	
	public void setBgColor(final UIColor color) {
		super.setBgColor(color);
		this.backgroundProperty.set(color);
	}
	
	public void redraw() {
		this.getControl().requestLayout();
	}
	
	public void addResizeListener(UIResizeListener listener) {
		if( this.resizeListener.isEmpty() ) {
			this.getControl().widthProperty().addListener(this.resizeListener);
			this.getControl().heightProperty().addListener(this.resizeListener);
		}
		this.resizeListener.addListener(listener);
	}

	public void removeResizeListener(UIResizeListener listener) {
		this.resizeListener.removeListener(listener);
		if( this.resizeListener.isEmpty() ) {
			this.getControl().widthProperty().removeListener(this.resizeListener);
			this.getControl().heightProperty().removeListener(this.resizeListener);
		}
	}
}
