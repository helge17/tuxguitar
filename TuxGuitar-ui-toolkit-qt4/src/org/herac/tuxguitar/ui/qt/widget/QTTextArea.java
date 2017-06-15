package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.event.UIModifyListener;
import org.herac.tuxguitar.ui.qt.event.QTModifyListenerManager;
import org.herac.tuxguitar.ui.widget.UITextArea;

import com.trolltech.qt.gui.QPlainTextEdit;

public class QTTextArea extends QTWidget<QPlainTextEdit> implements UITextArea {
	
	private Integer textLimit;
	private QTModifyListenerManager modifyListener;
	
	public QTTextArea(QTContainer parent, boolean vScroll, boolean hScroll) {
		super(new QPlainTextEdit(parent.getContainerControl()), parent);
		
		this.modifyListener = new QTModifyListenerManager(this);
	}

	public String getText() {
		return this.getControl().toPlainText();
	}

	public void setText(String text) {
		this.getControl().setPlainText(text);
	}
	
	public void append(String text) {
		this.getControl().appendPlainText(text);
	}
	
	public Integer getTextLimit() {
		return this.textLimit;
	}

	public void setTextLimit(Integer textLimit) {
		this.textLimit = textLimit;
	}
	
	public void addModifyListener(UIModifyListener listener) {
		if( this.modifyListener.isEmpty() ) {
			this.getControl().textChanged.connect(this.modifyListener, QTModifyListenerManager.SIGNAL_METHOD);
		}
		this.modifyListener.addListener(listener);
	}
	
	public void removeModifyListener(UIModifyListener listener) {
		this.modifyListener.removeListener(listener);
		if( this.modifyListener.isEmpty() ) {
			this.getControl().textChanged.disconnect();
		}
	}
}