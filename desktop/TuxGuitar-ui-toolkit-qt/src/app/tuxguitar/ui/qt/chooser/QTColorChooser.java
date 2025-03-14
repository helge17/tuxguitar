package app.tuxguitar.ui.qt.chooser;

import app.tuxguitar.ui.chooser.UIColorChooser;
import app.tuxguitar.ui.chooser.UIColorChooserHandler;
import app.tuxguitar.ui.qt.resource.QTColor;
import app.tuxguitar.ui.qt.widget.QTAbstractWindow;
import app.tuxguitar.ui.resource.UIColorModel;
import io.qt.gui.QColor;
import io.qt.widgets.QColorDialog;
import io.qt.widgets.QDialog;

public class QTColorChooser implements UIColorChooser {

	private QTAbstractWindow<?> window;
	private String text;
	private UIColorModel defaultModel;

	public QTColorChooser(QTAbstractWindow<?> window) {
		this.window = window;
	}

	public void choose(UIColorChooserHandler selectionHandler) {
		UIColorModel selection = null;

		QColorDialog dlg = new QColorDialog(this.window.getControl());
		if( this.text != null ) {
			dlg.setWindowTitle(this.text);
		}

		QColor defaultColor = null;
		if( this.defaultModel != null ) {
			defaultColor = new QTColor(this.defaultModel).getControl();
			dlg.setCurrentColor(defaultColor);
		}

		if( dlg.exec() == QDialog.DialogCode.Accepted.value() ) {
			QColor color = dlg.selectedColor();
			if( color != null ) {
				if( dlg.result() > 0 ) {
					selection = new UIColorModel(color.red(), color.green(), color.blue());
				}
				color.dispose();
			}
			if( defaultColor != null ) {
				defaultColor.dispose();
			}
		}
		selectionHandler.onSelectColor(selection);
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setDefaultModel(UIColorModel defaultModel) {
		this.defaultModel = defaultModel;
	}
}
