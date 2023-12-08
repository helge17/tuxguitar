package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.swt.event.SWTSelectionListenerManager;
import org.herac.tuxguitar.ui.widget.UISlider;

public class SWTSlider extends SWTControl<Slider> implements UISlider {
	
	private SWTSelectionListenerManager selectionListener;
	
	public SWTSlider(SWTContainer<? extends Composite> parent, int orientation) {
		super(new Slider(parent.getControl(), SWT.BORDER | orientation), parent);
		
		this.selectionListener = new SWTSelectionListenerManager(this);
	}

	public void setValue(int value) {
		this.getControl().setSelection(value);
	}

	public int getValue() {
		return this.getControl().getSelection();
	}

	public void setMaximum(int maximum) {
		this.getControl().setMaximum(maximum);
	}

	public int getMaximum() {
		return this.getControl().getMaximum();
	}

	public void setMinimum(int minimum) {
		this.getControl().setMinimum(minimum);
	}

	public int getMinimum() {
		return this.getControl().getMinimum();
	}

	public void setIncrement(int increment) {
		this.getControl().setIncrement(increment);
	}

	public int getIncrement() {
		return this.getControl().getIncrement();
	}
	
	public void setThumb(int thumb) {
		this.getControl().setThumb(thumb);
	}
	
	public int getThumb() {
		return this.getControl().getThumb();
	}
	
	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().addSelectionListener(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().removeSelectionListener(this.selectionListener);
		}
	}
}
