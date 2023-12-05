package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerChangeManager;
import org.herac.tuxguitar.ui.widget.UISpinner;

public class JFXSpinner extends JFXControl<Spinner<Integer>> implements UISpinner {
	
	private JFXSelectionListenerChangeManager<Number> selectionListener;
	private SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory;
	
	public JFXSpinner(JFXContainer<? extends Region> parent) {
		super(new Spinner<Integer>(), parent);
		
		this.selectionListener = new JFXSelectionListenerChangeManager<Number>(this);
		this.valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
		this.getControl().setValueFactory(this.valueFactory);
	}

	public void setValue(int value) {
		this.valueFactory.setValue(value);
	}

	public int getValue() {
		return this.valueFactory.getValue();
	}

	public void setMaximum(int maximum) {
		if( this.valueFactory.getMin() > maximum ) {
			this.valueFactory.setMin(maximum);
		}
		if( this.valueFactory.getValue() > maximum ) {
			this.valueFactory.setValue(maximum);
		}
		this.valueFactory.setMax(maximum);
	}

	public int getMaximum() {
		return this.valueFactory.getMax();
	}

	public void setMinimum(int minimum) {
		if( this.valueFactory.getMax() < minimum ) {
			this.valueFactory.setMax(minimum);
		}
		if( this.valueFactory.getValue() < minimum ) {
			this.valueFactory.setValue(minimum);
		}
		this.valueFactory.setMin(minimum);
	}

	public int getMinimum() {
		return this.valueFactory.getMin();
	}

	public void setIncrement(int increment) {
		this.valueFactory.setAmountToStepBy(increment);
	}

	public int getIncrement() {
		return this.valueFactory.getAmountToStepBy();
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
