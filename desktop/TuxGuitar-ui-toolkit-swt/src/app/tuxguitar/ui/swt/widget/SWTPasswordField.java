package app.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import app.tuxguitar.ui.widget.UIPasswordField;

public class SWTPasswordField extends SWTTextField implements UIPasswordField {

	public SWTPasswordField(SWTContainer<? extends Composite> parent) {
		super(parent, SWT.PASSWORD);
	}
}
