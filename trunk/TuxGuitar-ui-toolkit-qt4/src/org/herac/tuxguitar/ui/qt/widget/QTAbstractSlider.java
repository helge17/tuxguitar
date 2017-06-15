package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.qt.event.QTSelectionListenerManager;

import com.trolltech.qt.gui.QAbstractSlider;

public class QTAbstractSlider<T extends QAbstractSlider> extends QTWidget<T> {
	
	private QTSelectionListenerManager selectionListener;
	
	public QTAbstractSlider(T control, QTContainer parent) {
		super(control, parent);
		
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
	
	public void setThumb(int thumb) {
		this.getControl().setPageStep(thumb);
	}
	
	public int getThumb() {
		return this.getControl().pageStep();
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
