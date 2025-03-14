package app.tuxguitar.ui.qt.appearance;

import java.util.HashMap;
import java.util.Map;

import app.tuxguitar.ui.appearance.UIAppearance;
import app.tuxguitar.ui.appearance.UIColorAppearance;
import app.tuxguitar.ui.resource.UIColorModel;
import io.qt.gui.QColor;
import io.qt.gui.QPalette;
import io.qt.widgets.QApplication;

public class QTAppearance implements UIAppearance {

	private Map<UIColorAppearance, QColor> colorMap;

	public QTAppearance() {
		super();
	}

	public Map<UIColorAppearance, QColor> getColorMap() {
		if( this.colorMap == null ) {
			QPalette palette = QApplication.palette();

			this.colorMap = new HashMap<UIColorAppearance, QColor>();
			this.colorMap.put(UIColorAppearance.WidgetBackground, palette.window().color());
			this.colorMap.put(UIColorAppearance.WidgetForeground, palette.windowText().color());
			this.colorMap.put(UIColorAppearance.WidgetLightBackground, palette.alternateBase().color());
			this.colorMap.put(UIColorAppearance.WidgetLightForeground, palette.windowText().color());
			this.colorMap.put(UIColorAppearance.WidgetHighlightBackground, palette.light().color());
			this.colorMap.put(UIColorAppearance.WidgetHighlightForeground, palette.windowText().color());
			this.colorMap.put(UIColorAppearance.WidgetSelectedBackground, palette.midlight().color());
			this.colorMap.put(UIColorAppearance.WidgetSelectedForeground, palette.windowText().color());
			this.colorMap.put(UIColorAppearance.InputBackground, palette.base().color());
			this.colorMap.put(UIColorAppearance.InputForeground, palette.text().color());
			this.colorMap.put(UIColorAppearance.InputSelectedBackground, palette.midlight().color());
			this.colorMap.put(UIColorAppearance.InputSelectedForeground, palette.windowText().color());
		}
		return this.colorMap;
	}

	public UIColorModel getColorModel(UIColorAppearance colorAppearance) {
		if( this.getColorMap().containsKey(colorAppearance)) {
			QColor color = this.getColorMap().get(colorAppearance);

			return new UIColorModel(color.red(), color.green(), color.blue());
		}
		return new UIColorModel(0x00, 0x00, 0x00);
	}
}
