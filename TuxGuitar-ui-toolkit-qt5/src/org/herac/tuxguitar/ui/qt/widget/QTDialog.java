package org.herac.tuxguitar.ui.qt.widget;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.qtjambi.qt.core.Qt.WindowType;
import org.qtjambi.qt.widgets.QDialog;

public class QTDialog extends QTAbstractWindow<QDialog> {
	
	private boolean resizable;
	
	public QTDialog(QTAbstractWindow<?> parent, boolean modal, boolean resizable) {
		super(new QDialog(parent.getContainerControl()), parent);
		
		this.resizable = resizable;
		this.getControl().setModal(modal);
		this.setWindowFlags();
	}
	
	public void setWindowFlags() {
		List<WindowType> windowFlags = new ArrayList<WindowType>();
		windowFlags.add(WindowType.Dialog);
		windowFlags.add(WindowType.WindowCloseButtonHint);
		if( this.resizable ) {
			windowFlags.add(WindowType.WindowMaximizeButtonHint);
		}
		this.getControl().setWindowFlags(windowFlags.toArray(new WindowType[windowFlags.size()]));
	}
	
	public void join() {
		this.getControl().exec();
	}
	
	public void setBounds(UIRectangle bounds) {
		if(!this.resizable) {
			int width = Math.round(bounds.getWidth());
			int height = Math.round(bounds.getHeight());
			
			this.getControl().setMaximumSize(width, height);
			this.getControl().setMinimumSize(width, height);
		}
		super.setBounds(bounds);
	}
}