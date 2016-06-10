package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.ui.widget.UIWrapLabel;

public class SWTWrapLabel extends SWTLabel implements UIWrapLabel {
	
	private Float wrapWidth;
	
	public SWTWrapLabel(SWTContainer<? extends Composite> parent) {
		super(parent, SWT.WRAP);
	}

	public Float getWrapWidth() {
		return wrapWidth;
	}

	public void setWrapWidth(Float wrapWidth) {
		this.wrapWidth = wrapWidth;
	}
	
	public void computePackedSize() {
		super.computePackedSize();
		
		if( this.getWrapWidth() != null && this.getPackedSize().getWidth() > this.getWrapWidth() ) {
			this.computePackedSize(Math.round(this.getWrapWidth()), SWT.DEFAULT);
		}
	}
}
