package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.qt.event.QTSelectionListenerManager;
import org.herac.tuxguitar.ui.widget.UISpinner;

import com.trolltech.qt.gui.QSpinBox;

public class QTSpinner extends QTWidget<QSpinBox> implements UISpinner {
	
	private QTSelectionListenerManager selectionListener;
	
	public QTSpinner(QTContainer parent) {
		super(new QSpinBox(parent.getContainerControl()), parent);
		
		this.selectionListener = new QTSelectionListenerManager(this);
	}

	public void setValue(int value) {
		this.getControl().setValue(value);
	}

	public int getValue() {
		return this.getControl().value();
	}

	public void setMaximum(int maximum) {
		this.getControl().setMaximum(maximum);
	}

	public int getMaximum() {
		return this.getControl().maximum();
	}

	public void setMinimum(int minimum) {
		this.getControl().setMinimum(minimum);
	}

	public int getMinimum() {
		return this.getControl().minimum();
	}

	public void setIncrement(int increment) {
		this.getControl().setSingleStep(increment);
	}

	public int getIncrement() {
		return this.getControl().singleStep();
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().valueChanged.connect(this.selectionListener, QTSelectionListenerManager.SIGNAL_METHOD);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().valueChanged.disconnect();
		}
	}
}