package org.herac.tuxguitar.ui.qt.widget;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.qt.menu.QTMenuBar;
import org.herac.tuxguitar.ui.resource.UIRectangle;

import com.trolltech.qt.core.Qt.WindowType;
import com.trolltech.qt.gui.QDialog;

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
	
	public void setMenuBar(UIMenuBar menuBar) {
		if( menuBar != null ) {
			((QTMenuBar) menuBar).getControl().setParent(this.getControl());
		}
		super.setMenuBar(menuBar);
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