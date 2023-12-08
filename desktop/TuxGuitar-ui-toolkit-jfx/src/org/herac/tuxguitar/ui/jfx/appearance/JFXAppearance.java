package org.herac.tuxguitar.ui.jfx.appearance;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.ui.appearance.UIAppearance;
import org.herac.tuxguitar.ui.appearance.UIColorAppearance;
import org.herac.tuxguitar.ui.jfx.resource.JFXColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;

public class JFXAppearance implements UIAppearance {

	public static final String CSS_RESOURCE = "styles/styles.css";
	
	private Map<UIColorAppearance, String> colorMap;
	
	public JFXAppearance() {
		this.createColorResolvers();
	}
	
	public void createColorResolvers() {
		this.colorMap = new HashMap<UIColorAppearance, String>();
		this.colorMap.put(UIColorAppearance.WidgetBackground, "-fx-background");
		this.colorMap.put(UIColorAppearance.WidgetForeground, "-fx-text-background-color");
		this.colorMap.put(UIColorAppearance.WidgetLightBackground, "-fx-background");
		this.colorMap.put(UIColorAppearance.WidgetLightForeground, "-fx-text-background-color");
		this.colorMap.put(UIColorAppearance.WidgetHighlightBackground, "-fx-shadow-highlight-color");
		this.colorMap.put(UIColorAppearance.WidgetHighlightForeground, "-fx-text-background-color");
		this.colorMap.put(UIColorAppearance.WidgetSelectedBackground, "-fx-pressed-base");
		this.colorMap.put(UIColorAppearance.WidgetSelectedForeground, "-fx-text-background-color");
		this.colorMap.put(UIColorAppearance.InputBackground, "-fx-control-inner-background");
		this.colorMap.put(UIColorAppearance.InputForeground, "-fx-text-inner-color");
		this.colorMap.put(UIColorAppearance.InputSelectedBackground, "-fx-selection-bar");
		this.colorMap.put(UIColorAppearance.InputSelectedForeground, "-fx-selection-bar-text");
	}
	
	public UIColorModel getColorModel(UIColorAppearance colorAppearance) {
		if( this.colorMap.containsKey(colorAppearance)) {
			return new JFXColor(new JFXStyleableColor(this.colorMap.get(colorAppearance)).getColor()).getControl();
		}
		return new UIColorModel(0x00, 0x00, 0x00);
	}
}
