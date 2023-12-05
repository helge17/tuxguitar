package org.herac.tuxguitar.ui.jfx.widget;

import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerChangeManager;
import org.herac.tuxguitar.ui.widget.UIScale;

public class JFXScale extends JFXControl<Slider> implements UIScale {
	
	private JFXSelectionListenerChangeManager<Number> selectionListener;
	
	public JFXScale(JFXContainer<? extends Region> parent, Orientation orientation) {
		super(new Slider(), parent);
		
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
		if( this.getControl().getMin() > maximum ) {
			this.getControl().setMin(maximum);
		}
		if( this.getControl().getValue() > maximum ) {
			this.getControl().setValue(maximum);
		}
		this.getControl().setMax(maximum);
	}

	public int getMaximum() {
		return (int) Math.round(this.getControl().getMax());
	}

	public void setMinimum(int minimum) {
		if( this.getControl().getMax() < minimum ) {
			this.getControl().setMax(minimum);
		}
		if( this.getControl().getValue() < minimum ) {
			this.getControl().setValue(minimum);
		}
		this.getControl().setMin(minimum);
	}

	public int getMinimum() {
		return (int) Math.round(this.getControl().getMin());
	}

	public void setIncrement(int increment) {
		this.getControl().setBlockIncrement(increment);
	}

	public int getIncrement() {
		return (int) Math.round(this.getControl().getBlockIncrement());
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
