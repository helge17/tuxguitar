package app.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import app.tuxguitar.ui.widget.UILabel;

public class SWTLabel extends SWTControl<Label> implements UILabel {

	public SWTLabel(SWTContainer<? extends Composite> parent, int style) {
		super(new Label(parent.getControl(), style), parent);
	}

	public SWTLabel(SWTContainer<? extends Composite> parent) {
		this(parent, SWT.NORMAL);
	}

	public String getText() {
		return this.getControl().getText();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}
}
