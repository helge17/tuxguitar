package org.herac.tuxguitar.ui.qt.resource;

import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontAlignment;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.qtjambi.qt.gui.QFont;

public class QTFont extends QTComponent<QFont> implements UIFont {
	
	private UIFontAlignment alignment;
	
	public QTFont(QFont font) {
		super(font);
	}
	
	public QTFont(UIFontModel fm){
		this(new QFont(QTFont.checkName(fm.getName()), Math.round(fm.getHeight()), (fm.isBold() ? QFont.Weight.Bold : QFont.Weight.Normal).value(), fm.isItalic()));
		
		this.alignment = fm.getAlignment();
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
	
	public UIFontAlignment getAlignment() {
		if( this.alignment == null ) {
			this.alignment = new UIFontAlignment();
			this.alignment.setTop(this.getControl().pointSize());
			this.alignment.setMiddle(this.alignment.getTop() / 2f);
			this.alignment.setBottom(0f);
		}
		return this.alignment;
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
