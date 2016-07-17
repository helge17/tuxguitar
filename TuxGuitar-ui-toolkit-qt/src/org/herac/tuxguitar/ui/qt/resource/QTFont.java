package org.herac.tuxguitar.ui.qt.resource;

import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;

import com.trolltech.qt.gui.QFont;

public class QTFont extends QTComponent<QFont> implements UIFont {
	
	public QTFont(QFont font) {
		super(font);
	}
	
	public QTFont(String name, float height, boolean bold, boolean italic) {
		super(new QFont(QTFont.checkName(name), Math.round(height), (bold ? QFont.Weight.Bold : QFont.Weight.Normal).value(), italic));
	}
	
	public QTFont(UIFontModel model){
		this(model.getName(), model.getHeight(), model.isBold(), model.isItalic());
	}
	
	public String getName() {
		return this.getControl().family();
	}
	
	public float getHeight() {
		return this.getControl().pointSize();
	}
	
	public boolean isBold() {
		return this.getControl().bold();
	}
	
	public boolean isItalic() {
		return this.getControl().italic();
	}
	
	public UIFontModel toModel() {
		return new UIFontModel(this.getName(), this.getHeight(), this.isBold(), this.isItalic());
	}
	
	public static String checkName(String name) {
		if( name != null && name.length() > 0 && !UIFontModel.DEFAULT_NAME.equals(name)) {
			return name;
		}
		QFont defaultFont = new QFont();
		String defaultFamily = defaultFont.defaultFamily();
		defaultFont.dispose();
		
		return defaultFamily;
	}
}
