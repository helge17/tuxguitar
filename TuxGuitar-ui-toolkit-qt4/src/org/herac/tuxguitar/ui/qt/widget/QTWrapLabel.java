package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIWrapLabel;

public class QTWrapLabel extends QTLabel implements UIWrapLabel {
	
	private Float wrapWidth;
	
	public QTWrapLabel(QTContainer parent) {
		super(parent);
	}
	
	public Float getWrapWidth() {
		return this.wrapWidth;
	}
	
	public void setWrapWidth(Float wrapWidth) {
		this.wrapWidth = wrapWidth;
	}
	
	public void computePackedSize() {
		this.getControl().setWordWrap(false);
		
		super.computePackedSize();
		
		UISize packedSize = this.getPackedSize();
		if( this.getWrapWidth() != null && packedSize.getWidth() > this.getWrapWidth() ) {
			this.getControl().setWordWrap(true);
			
			packedSize.setWidth(this.getWrapWidth());
			packedSize.setHeight(this.getControl().heightForWidth(this.getWrapWidth().intValue()));
			
			this.setPackedSize(packedSize);
		}
	}
}