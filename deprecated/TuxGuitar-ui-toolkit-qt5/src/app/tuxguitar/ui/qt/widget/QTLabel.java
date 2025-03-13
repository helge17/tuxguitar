package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.widget.UILabel;
import org.qtjambi.qt.widgets.QLabel;

public class QTLabel extends QTWidget<QLabel> implements UILabel {

	public QTLabel(QTContainer parent) {
		super(new QLabel(parent.getContainerControl()), parent);
	}

	public String getText() {
		return this.getControl().text();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}
}