package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIWrapLabel;

public class QTWrapLabel extends QTLabel implements UIWrapLabel {
	
	public QTWrapLabel(QTContainer parent) {
		super(parent);
	}
	
	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		this.getControl().setWordWrap(fixedWidth != null);
		
		Float packedHeight = fixedHeight;
		if( packedHeight == null && fixedWidth != null ) {
			packedHeight = (float) this.getControl().heightForWidth(fixedWidth.intValue());
		}
		super.computePackedSize(fixedWidth, packedHeight);
	}
}