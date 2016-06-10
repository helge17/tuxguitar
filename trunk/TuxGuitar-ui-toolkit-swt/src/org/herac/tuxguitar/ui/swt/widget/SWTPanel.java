package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.widget.UIPanel;

public class SWTPanel extends SWTLayoutContainer<Composite> implements UIPanel {
	
	public SWTPanel(SWTContainer<? extends Composite> parent, boolean bordered) {
		super(new Composite(parent.getControl(), (bordered ? SWT.BORDER : SWT.NONE)), parent);
	}
}
