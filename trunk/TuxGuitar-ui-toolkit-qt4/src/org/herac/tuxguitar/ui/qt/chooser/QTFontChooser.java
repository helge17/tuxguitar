package org.herac.tuxguitar.ui.qt.chooser;

import org.herac.tuxguitar.ui.chooser.UIFontChooser;
import org.herac.tuxguitar.ui.chooser.UIFontChooserHandler;
import org.herac.tuxguitar.ui.qt.resource.QTFont;
import org.herac.tuxguitar.ui.qt.widget.QTAbstractWindow;
import org.herac.tuxguitar.ui.resource.UIFontModel;

import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QFontDialog;

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
