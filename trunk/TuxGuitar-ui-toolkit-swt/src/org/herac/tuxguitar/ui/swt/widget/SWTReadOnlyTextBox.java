package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.widget.UIReadOnlyTextBox;

public class SWTReadOnlyTextBox extends SWTText implements UIReadOnlyTextBox {
	
	public SWTReadOnlyTextBox(SWTContainer<? extends Composite> parent, boolean vScroll, boolean hScroll) {
		super(parent, SWT.MULTI | SWT.WRAP | (vScroll ? SWT.V_SCROLL : 0) | (hScroll ? SWT.H_SCROLL : 0));
		
		this.getControl().setEditable(false);
	}
}
