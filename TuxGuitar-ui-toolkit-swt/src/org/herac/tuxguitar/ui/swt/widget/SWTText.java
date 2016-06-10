package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.ui.resource.UISize;

public class SWTText extends SWTControl<Text> {
	
	public SWTText(SWTContainer<? extends Composite> parent, int style) {
		super(new Text(parent.getControl(), SWT.BORDER  | style), parent);
		
		this.computePackedSize();
	}

	public String getText() {
		return this.getControl().getText();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}
	
	public void append(String text) {
		this.getControl().append(text);
	}
	
	public Integer getTextLimit() {
		return this.getControl().getTextLimit();
	}
	
	public void setTextLimit(Integer limit) {
		this.getControl().setTextLimit(limit);
	}
	
	public void computePackedSize() {
		UISize packedSize = this.getPackedSize();
		if( packedSize.getWidth() == 0f && packedSize.getHeight() == 0f ) {
			super.computePackedSize();
		}
	}
}
