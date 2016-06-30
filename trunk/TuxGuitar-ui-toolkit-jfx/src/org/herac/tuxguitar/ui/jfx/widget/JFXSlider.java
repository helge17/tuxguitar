package org.herac.tuxguitar.ui.jfx.widget;

import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerChangeManager;
import org.herac.tuxguitar.ui.widget.UISlider;

public class JFXSlider extends JFXControl<ScrollBar> implements UISlider {
	
	private JFXSelectionListenerChangeManager<Number> selectionListener;
	
	public JFXSlider(JFXContainer<? extends Region> parent, Orientation orientation) {
		super(new ScrollBar(), parent);
		
		this.getControl().setOrientation(orientation);
		this.selectionListener = new JFXSelectionListenerChangeManager<Number>(this);
	}

	public void setValue(int value) {
		this.getControl().setValue(value);
	}

	public int getValue() {
		return (int) Math.round(this.getControl().getValue());
	}

	public void setMaximum(int maximum) {
		this.getControl().setMax(maximum);
	}

	public int getMaximum() {
		return (int) Math.round(this.getControl().getMax());
	}

	public void setMinimum(int minimum) {
		this.getControl().setMin(minimum);
	}

	public int getMinimum() {
		return (int) Math.round(this.getControl().getMin());
	}

	public void setIncrement(int increment) {
		this.getControl().setUnitIncrement(increment);
	}

	public int getIncrement() {
		return (int) Math.round(this.getControl().getUnitIncrement());
	}
	
	public void setThumb(int thumb) {
		this.getControl().setVisibleAmount(thumb);
	}
	
	public int getThumb() {
		return (int) Math.round(this.getControl().getVisibleAmount());
	}
	
	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().valueProperty().addListener(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().valueProperty().removeListener(this.selectionListener);
		}
	}
}
