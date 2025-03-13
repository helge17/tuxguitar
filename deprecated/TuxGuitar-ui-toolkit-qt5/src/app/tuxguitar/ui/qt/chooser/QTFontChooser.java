package app.tuxguitar.ui.qt.chooser;

import app.tuxguitar.ui.chooser.UIFontChooser;
import app.tuxguitar.ui.chooser.UIFontChooserHandler;
import app.tuxguitar.ui.qt.resource.QTFont;
import app.tuxguitar.ui.qt.widget.QTAbstractWindow;
import app.tuxguitar.ui.resource.UIFontModel;
import org.qtjambi.qt.gui.QFont;
import org.qtjambi.qt.widgets.QDialog;
import org.qtjambi.qt.widgets.QFontDialog;

public class QTFontChooser implements UIFontChooser {

	private QTAbstractWindow<?> window;
	private String text;
	private UIFontModel defaultModel;

	public QTFontChooser(QTAbstractWindow<?> window) {
		this.window = window;
	}

	public void choose(final UIFontChooserHandler selectionHandler) {
		UIFontModel selection = null;

		QFontDialog dlg = new QFontDialog(this.window.getControl());
		if( this.text != null ) {
			dlg.setWindowTitle(this.text);
		}

		QFont defaultFont = null;
		if( this.defaultModel != null ) {
			defaultFont = new QTFont(this.defaultModel).getControl();
			dlg.setCurrentFont(defaultFont);
		}

		if( dlg.exec() == QDialog.DialogCode.Accepted.value() ) {
			QFont font = dlg.selectedFont();
			if( font != null ) {
				if( dlg.result() > 0 ) {
					selection = new QTFont(font).toModel();
				}
				font.dispose();
			}
			if( defaultFont != null ) {
				defaultFont.dispose();
			}
		}
		selectionHandler.onSelectFont(selection);
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setDefaultModel(UIFontModel defaultModel) {
		this.defaultModel = defaultModel;
	}
}
