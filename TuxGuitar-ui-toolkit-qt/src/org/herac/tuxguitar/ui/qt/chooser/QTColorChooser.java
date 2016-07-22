package org.herac.tuxguitar.ui.qt.chooser;

import org.herac.tuxguitar.ui.chooser.UIColorChooser;
import org.herac.tuxguitar.ui.chooser.UIColorChooserHandler;
import org.herac.tuxguitar.ui.qt.resource.QTColor;
import org.herac.tuxguitar.ui.qt.widget.QTAbstractWindow;
import org.herac.tuxguitar.ui.resource.UIColorModel;

import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QColorDialog;
import com.trolltech.qt.gui.QDialog;

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
