package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIDivider;

import com.trolltech.qt.gui.QWidget;

public class QTDivider extends QTWidget<QWidget> implements UIDivider {
	
	private static final float DEFAULT_PACKED_SIZE = 2f;
	
	public QTDivider(QTContainer parent) {
		super(new QWidget(parent.getContainerControl()), parent);
	}
	
	@Override
	public void computePackedSize() {
		this.setPackedSize(new UISize(DEFAULT_PACKED_SIZE, DEFAULT_PACKED_SIZE));
	}
}
