package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.herac.tuxguitar.ui.widget.UISeparator;

public class SWTSeparator extends SWTControl<Label> implements UISeparator {
	
	public SWTSeparator(SWTContainer<? extends Composite> parent, int orientationStyle) {
		super(new Label(parent.getControl(), SWT.SEPARATOR | orientationStyle), parent);
	}
}
