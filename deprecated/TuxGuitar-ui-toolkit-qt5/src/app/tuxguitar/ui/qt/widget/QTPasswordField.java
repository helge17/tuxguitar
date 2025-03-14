package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.widget.UIPasswordField;
import org.qtjambi.qt.widgets.QLineEdit.EchoMode;

public class QTPasswordField extends QTTextField implements UIPasswordField {

	public QTPasswordField(QTContainer parent) {
		super(parent);

		this.getControl().setEchoMode(EchoMode.Password);
	}
}