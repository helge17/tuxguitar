package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIDivider;

public class SWTDivider extends SWTControl<Composite> implements UIDivider {
	
	private static final float DEFAULT_PACKED_SIZE = 2f;
	
	public SWTDivider(SWTContainer<? extends Composite> parent) {
		super(new Composite(parent.getControl(), SWT.NONE), parent);
	}
	
	@Override
	public void computePackedSize() {
		this.setPackedSize(new UISize(DEFAULT_PACKED_SIZE, DEFAULT_PACKED_SIZE));
	}
}
