package org.herac.tuxguitar.ui.jfx.widget;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerChangeManager;

import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;

public abstract class JFXAbstractScrollBar extends JFXControl<ScrollBar> {
	
	private Integer thumb;
	private JFXSelectionListenerChangeManager<Number> selectionListener;
	
	public JFXAbstractScrollBar(JFXContainer<? extends Region> parent, Orientation orientation) {
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
		if( this.getControl().getMin() > maximum ) {
			this.getControl().setMin(maximum);
		}
		if( this.getControl().getValue() > maximum ) {
			this.getControl().setValue(maximum);
		}
		this.getControl().setMax(maximum);
		this.updateVisibleAmount();
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
		this.updateVisibleAmount();
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
		this.thumb = thumb;
		this.updateVisibleAmount();
	}
	
	public int getThumb() {
		return (this.thumb != null ? this.thumb : -1);
	}
	
	public void updateVisibleAmount() {
		double amount = 0;
		if( this.thumb != null ) {
			double maximumValue = this.getControl().getMax();
			double maximumSize = (this.thumb + maximumValue);
			if( maximumSize > 0 ) {
				amount = (this.thumb * maximumValue / maximumSize);
			}
		}
		this.getControl().setVisibleAmount(amount);
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
