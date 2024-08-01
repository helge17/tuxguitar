package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIDivider;
import org.qtjambi.qt.widgets.QWidget;

public class QTDivider extends QTWidget<QWidget> implements UIDivider {
	
	private static final float DEFAULT_PACKED_SIZE = 2f;
	
	public QTDivider(QTContainer parent) {
		super(new QWidget(parent.getContainerControl()), parent);
	}
	
	@Override
	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		this.setPackedSize(new UISize(fixedWidth != null ? fixedWidth : DEFAULT_PACKED_SIZE, fixedHeight != null ? fixedHeight : DEFAULT_PACKED_SIZE));
	}
}
