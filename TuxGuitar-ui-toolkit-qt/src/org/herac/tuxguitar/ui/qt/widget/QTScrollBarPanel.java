package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.widget.UIScrollBar;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;

import com.trolltech.qt.core.Qt.ScrollBarPolicy;
import com.trolltech.qt.gui.QAbstractScrollArea;
import com.trolltech.qt.gui.QWidget;

public class QTScrollBarPanel extends QTAbstractPanel<QAbstractScrollArea> implements UIScrollBarPanel {
	
	private QTScrollBar vScrollBar;
	private QTScrollBar hScrollBar;
	
	public QTScrollBarPanel(QTContainer parent, boolean vScroll, boolean hScroll, boolean bordered) {
		super(new QAbstractScrollArea(parent.getContainerControl()), parent, bordered);
		
		if( vScroll ) {
			this.getControl().setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOn);
			this.vScrollBar = new QTScrollBar(this.getControl().verticalScrollBar(), null);
		}
		if( hScroll ) {
			this.getControl().setHorizontalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOn);
			this.hScrollBar = new QTScrollBar(this.getControl().horizontalScrollBar(), null);
		}
	}

	@Override
	public QWidget getContainerControl() {
		return this.getControl().viewport();
	}
	
	@Override
	public UIScrollBar getHScroll() {
		return this.hScrollBar;
	}

	@Override
	public UIScrollBar getVScroll() {
		return this.vScrollBar;
	}
}
