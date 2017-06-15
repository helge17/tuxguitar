package org.herac.tuxguitar.ui.qt.toolbar;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.qt.event.QTSelectionListenerManager;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;

import com.trolltech.qt.gui.QToolButton;

public class QTToolActionItem extends QTToolAbstractButtonItem<QToolButton> implements UIToolActionItem {
	
	private QTSelectionListenerManager selectionListener;
	
	public QTToolActionItem(QTToolBar parent) {
		super(new QToolButton(parent.getControl()), parent);
		
		this.selectionListener = new QTSelectionListenerManager(this);
	}
	
	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().clicked.connect(this.selectionListener, QTSelectionListenerManager.SIGNAL_METHOD);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().clicked.disconnect();
		}
	}
}
