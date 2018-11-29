package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.app.system.icons.TGSkinManager;
import org.herac.tuxguitar.app.system.properties.TGPropertiesUIUtil;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.ui.appearance.UIAppearance;
import org.herac.tuxguitar.ui.appearance.UIColorAppearance;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;

public class TGTableColorModel {
	
	private UIColorModel[] backgrounds;
	private UIColorModel[] foregrounds;
	
	public TGTableColorModel() {
		super();
	}
	
	public void resetColors(TGContext context) {
		UIAppearance appearance = TGApplication.getInstance(context).getAppearance();;
		TGProperties properties = TGSkinManager.getInstance(context).getCurrentSkinProperties();
		
		UIColorModel[] defaultForegrounds = new UIColorModel[] {
			appearance.getColorModel(UIColorAppearance.WidgetLightForeground),
			appearance.getColorModel(UIColorAppearance.WidgetHighlightForeground),
			appearance.getColorModel(UIColorAppearance.WidgetSelectedForeground),
			appearance.getColorModel(UIColorAppearance.WidgetSelectedForeground),
		};
		
		UIColorModel[] defaultBackgrounds = new UIColorModel[] {
			appearance.getColorModel(UIColorAppearance.WidgetLightBackground),
			appearance.getColorModel(UIColorAppearance.WidgetHighlightBackground),
			appearance.getColorModel(UIColorAppearance.WidgetSelectedBackground),
			appearance.getColorModel(UIColorAppearance.WidgetSelectedBackground),
		};
		
		this.foregrounds = new UIColorModel[defaultForegrounds.length];
		for(int i = 0 ; i < this.foregrounds.length; i ++) {
			this.foregrounds[i] = TGPropertiesUIUtil.getColorModelValue(context, properties, ("table.foreground." + i), defaultForegrounds[i]);
		}
		
		this.backgrounds = new UIColorModel[defaultBackgrounds.length];
		for(int i = 0 ; i < this.backgrounds.length; i ++) {
			this.backgrounds[i] = TGPropertiesUIUtil.getColorModelValue(context, properties, ("table.background." + i), defaultBackgrounds[i]);
		}
	}
	
	public UIColor createForeground(TGContext context, int index) {
		return TGApplication.getInstance(context).getFactory().createColor(this.foregrounds[index]);
	}
	
	public UIColor createBackground(TGContext context, int index) {
		return TGApplication.getInstance(context).getFactory().createColor(this.backgrounds[index]);
	}
	
	public UIColor[] createForegrounds(TGContext context) {
		UIColor[] colors = new UIColor[this.foregrounds.length];
		for(int i = 0 ; i < this.foregrounds.length; i ++) {
			colors[i] = this.createForeground(context, i);
		}
		return colors;
	}
	
	public UIColor[] createBackgrounds(TGContext context) {
		UIColor[] colors = new UIColor[this.backgrounds.length];
		for(int i = 0 ; i < this.backgrounds.length; i ++) {
			colors[i] = this.createBackground(context, i);
		}
		return colors;
	}
}
