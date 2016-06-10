package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.herac.tuxguitar.ui.widget.UILegendPanel;

public class SWTLegendPanel extends SWTLayoutContainer<Group> implements UILegendPanel {
	
	public SWTLegendPanel(SWTContainer<? extends Composite> parent) {
		super(new Group(parent.getControl(), SWT.SHADOW_ETCHED_IN), parent);
	}

	public String getText() {
		return this.getControl().getText();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}
}
