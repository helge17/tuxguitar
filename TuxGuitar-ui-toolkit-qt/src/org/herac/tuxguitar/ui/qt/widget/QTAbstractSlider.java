package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.qt.event.QTSelectionListenerManager;

import com.trolltech.qt.gui.QAbstractSlider;

public class QTAbstractSlider<T extends QAbstractSlider> extends QTWidget<T> {
	
	private int thumb;
	private int maximum;
	private QTSelectionListenerManager selectionListener;
	
	public QTAbstractSlider(T control, QTContainer parent) {
		super(control, parent);
		
		this.thumb = 0;
		this.maximum = 0;
		this.selectionListener = new QTSelectionListenerManager(this);
	}
	
	public void setValue(int value) {
		this.getControl().setValue(value);
	}

	public int getValue() {
		return this.getControl().value();
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
		this.updateMaximum();
	}

	public int getMaximum() {
		return this.maximum;
	}

	public void setMinimum(int minimum) {
		this.getControl().setMinimum(minimum);
		this.updateMaximum();
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
		this.thumb = thumb;
		this.updateMaximum();
	}
	
	public int getThumb() {
		return this.thumb;
	}
	
	public void updateMaximum() {
		int range = ((this.getControl().maximum() - this.getControl().minimum()) + this.getControl().pageStep());
		
		this.getControl().setPageStep(this.thumb > range? range : this.thumb);
		this.getControl().setMaximum(Math.max(this.maximum - this.thumb, 0));
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
