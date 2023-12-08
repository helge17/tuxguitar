package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.herac.tuxguitar.ui.widget.UIProgressBar;

public class SWTProgressBar extends SWTControl<ProgressBar> implements UIProgressBar {
	
	public SWTProgressBar(SWTContainer<? extends Composite> parent) {
		super(new ProgressBar(parent.getControl(), SWT.BORDER | SWT.HORIZONTAL | SWT.SMOOTH), parent);
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
}
