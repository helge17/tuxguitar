package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.event.UIModifyListener;
import org.herac.tuxguitar.ui.qt.event.QTModifyListenerManager;
import org.herac.tuxguitar.ui.widget.UITextField;

import com.trolltech.qt.gui.QLineEdit;

public class QTTextField extends QTWidget<QLineEdit> implements UITextField {
	
	private QTModifyListenerManager modifyListener;
	
	public QTTextField(QTContainer parent) {
		super(new QLineEdit(parent.getContainerControl()), parent);
		
		this.modifyListener = new QTModifyListenerManager(this);
	}

	public String getText() {
		return this.getControl().text();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}

	@Override
	public Integer getTextLimit() {
		return this.getControl().maxLength();
	}

	@Override
	public void setTextLimit(Integer limit) {
		this.getControl().setMaxLength(limit);
	}

	@Override
	public void addModifyListener(UIModifyListener listener) {
		if( this.modifyListener.isEmpty() ) {
			this.getControl().textChanged.connect(this.modifyListener, QTModifyListenerManager.SIGNAL_METHOD);
		}
		this.modifyListener.addListener(listener);
	}

	@Override
	public void removeModifyListener(UIModifyListener listener) {
		this.modifyListener.removeListener(listener);
		if( this.modifyListener.isEmpty() ) {
			this.getControl().textChanged.disconnect();
		}
	}
}