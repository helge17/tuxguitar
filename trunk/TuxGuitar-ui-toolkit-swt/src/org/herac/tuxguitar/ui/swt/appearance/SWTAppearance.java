package org.herac.tuxguitar.ui.swt.appearance;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.herac.tuxguitar.ui.appearance.UIAppearance;
import org.herac.tuxguitar.ui.appearance.UIColorAppearance;
import org.herac.tuxguitar.ui.resource.UIColorModel;

public class SWTAppearance implements UIAppearance {

	private Display display;
	private Map<UIColorAppearance, UIColorModel> colorMap;
	
	public SWTAppearance(Display display) {
		this.display = display;
		this.createColorResolvers();
	}
	
	public void createColorResolvers() {
		this.colorMap = new HashMap<UIColorAppearance, UIColorModel>();
		this.colorMap.put(UIColorAppearance.WidgetBackground, this.createColorModel(SWT.COLOR_WIDGET_BACKGROUND));
		this.colorMap.put(UIColorAppearance.WidgetForeground, this.createColorModel(SWT.COLOR_WIDGET_FOREGROUND));
		this.colorMap.put(UIColorAppearance.WidgetLightBackground, this.createColorModel(SWT.COLOR_WIDGET_BACKGROUND, SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		this.colorMap.put(UIColorAppearance.WidgetLightForeground, this.createColorModel(SWT.COLOR_WIDGET_FOREGROUND));
		this.colorMap.put(UIColorAppearance.WidgetHighlightBackground, this.createColorModel(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		this.colorMap.put(UIColorAppearance.WidgetHighlightForeground, this.createColorModel(SWT.COLOR_WIDGET_FOREGROUND));
		this.colorMap.put(UIColorAppearance.WidgetSelectedBackground, this.createColorModel(SWT.COLOR_WIDGET_BACKGROUND, SWT.COLOR_WIDGET_NORMAL_SHADOW));
		this.colorMap.put(UIColorAppearance.WidgetSelectedForeground, this.createColorModel(SWT.COLOR_LIST_SELECTION_TEXT));
		this.colorMap.put(UIColorAppearance.InputBackground, this.createColorModel(SWT.COLOR_LIST_BACKGROUND));
		this.colorMap.put(UIColorAppearance.InputForeground, this.createColorModel(SWT.COLOR_LIST_FOREGROUND));
		this.colorMap.put(UIColorAppearance.InputSelectedBackground, this.createColorModel(SWT.COLOR_LIST_SELECTION));
		this.colorMap.put(UIColorAppearance.InputSelectedForeground, this.createColorModel(SWT.COLOR_LIST_SELECTION_TEXT));
	}
	
	public UIColorModel createColorModel(int style) {
		Color color = this.display.getSystemColor(style);
		
		return new UIColorModel(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public UIColorModel createColorModel(int style1, int style2) {
		Color c1 = this.display.getSystemColor(style1);
		Color c2 = this.display.getSystemColor(style2);
		
		return new UIColorModel(((c1.getRed() + c2.getRed()) / 2), ((c1.getGreen() + c2.getGreen()) / 2), ((c1.getBlue() + c2.getBlue()) / 2));
	}
	
//	public UIColorModel getColorModel(UIColorAppearance colorAppearance) {
//		if( UIColorAppearance.WidgetLightBackground.equals(colorAppearance)) {
//			Color c1 = this.display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
//			Color c2 = this.display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
//			
//			return new UIColorModel(((c1.getRed() + c2.getRed()) / 2), ((c1.getGreen() + c2.getGreen()) / 2), ((c1.getBlue() + c2.getBlue()) / 2));
//		}
//		
//		if( this.colorMap.containsKey(colorAppearance)) {
//			Color color = this.display.getSystemColor(this.colorMap.get(colorAppearance));
//			return new UIColorModel(color.getRed(), color.getGreen(), color.getBlue());
//		}
//		return new UIColorModel(0x00, 0x00, 0x00);
//	}
	
	public UIColorModel getColorModel(UIColorAppearance colorAppearance) {
		if( this.colorMap.containsKey(colorAppearance)) {
			return this.colorMap.get(colorAppearance);
		}
		return new UIColorModel(0x00, 0x00, 0x00);
	}
}
