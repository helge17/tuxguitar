package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.swt.event.SWTSelectionListenerManager;
import org.herac.tuxguitar.ui.widget.UIScale;

public class SWTScale extends SWTControl<Scale> implements UIScale {
	
	private SWTSelectionListenerManager selectionListener;
	
	public SWTScale(SWTContainer<? extends Composite> parent, int orientation) {
		super(new Scale(parent.getControl(), SWT.BORDER | orientation), parent);
		
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
