package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.event.UIModifyListener;
import app.tuxguitar.ui.qt.event.QTModifyListenerManager;
import app.tuxguitar.ui.widget.UITextField;
import io.qt.widgets.QLineEdit;

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