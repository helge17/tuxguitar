package app.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import app.tuxguitar.ui.widget.UIIndeterminateProgressBar;

public class SWTIndeterminateProgressBar extends SWTControl<ProgressBar> implements UIIndeterminateProgressBar {

	public SWTIndeterminateProgressBar(SWTContainer<? extends Composite> parent) {
		super(new ProgressBar(parent.getControl(), SWT.BORDER | SWT.HORIZONTAL | SWT.SMOOTH | SWT.INDETERMINATE), parent);
	}
}
