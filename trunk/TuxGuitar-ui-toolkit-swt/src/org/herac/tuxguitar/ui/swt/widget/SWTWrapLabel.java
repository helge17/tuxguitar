package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.widget.UIWrapLabel;

public class SWTWrapLabel extends SWTLabel implements UIWrapLabel {
	
	public SWTWrapLabel(SWTContainer<? extends Composite> parent) {
		super(parent, SWT.WRAP);
	}
}
