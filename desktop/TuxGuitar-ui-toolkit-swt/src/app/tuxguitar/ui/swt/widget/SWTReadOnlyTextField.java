package app.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import app.tuxguitar.ui.widget.UIReadOnlyTextField;

public class SWTReadOnlyTextField extends SWTText implements UIReadOnlyTextField {

	public SWTReadOnlyTextField(SWTContainer<? extends Composite> parent) {
		super(parent, SWT.READ_ONLY);
	}
}
