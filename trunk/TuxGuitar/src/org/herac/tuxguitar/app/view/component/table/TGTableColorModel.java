package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.app.system.icons.TGSkinManager;
import org.herac.tuxguitar.app.system.properties.TGPropertiesUIUtil;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;

public class TGTableColorModel {
	
	private static final UIColorModel[] DEFAULT_BACKGROUNDS = new UIColorModel[] {
		new UIColorModel(255, 255, 255),
		new UIColorModel(238, 238, 238),
		new UIColorModel(192, 192, 192),
	};
	
	private static final UIColorModel[] DEFAULT_FOREGROUNDS = new UIColorModel[] {
		new UIColorModel(0, 0, 0),
		new UIColorModel(0, 0, 0),
		new UIColorModel(0, 0, 0),
	};
	
	private UIColorModel[] backgrounds;
	private UIColorModel[] foregrounds;
	
	public TGTableColorModel() {
		super();
	}
	
	public void resetColors(TGContext context) {
		TGProperties properties = TGSkinManager.getInstance(context).getCurrentSkinProperties();
		
		this.foregrounds = new UIColorModel[DEFAULT_FOREGROUNDS.length];
		for(int i = 0 ; i < this.foregrounds.length; i ++) {
			this.foregrounds[i] = TGPropertiesUIUtil.getColorModelValue(properties, ("table.foreground." + i), DEFAULT_FOREGROUNDS[i]);
		}
		
		this.backgrounds = new UIColorModel[DEFAULT_BACKGROUNDS.length];
		for(int i = 0 ; i < this.backgrounds.length; i ++) {
			this.backgrounds[i] = TGPropertiesUIUtil.getColorModelValue(properties, ("table.background." + i), DEFAULT_BACKGROUNDS[i]);
		}
	}
	
	public UIColor createForeground(TGContext context, int index) {
		return TGApplication.getInstance(context).getFactory().createColor(this.foregrounds[index]);
	}
	
	public UIColor createBackground(TGContext context, int index) {
		return TGApplication.getInstance(context).getFactory().createColor(this.backgrounds[index]);
	}
	
	public UIColor[] createForegrounds(TGContext context) {
		UIColor[] colors = new UIColor[DEFAULT_FOREGROUNDS.length];
		for(int i = 0 ; i < this.foregrounds.length; i ++) {
			colors[i] = this.createForeground(context, i);
		}
		return colors;
	}
	
	public UIColor[] createBackgrounds(TGContext context) {
		UIColor[] colors = new UIColor[DEFAULT_BACKGROUNDS.length];
		for(int i = 0 ; i < this.backgrounds.length; i ++) {
			colors[i] = this.createBackground(context, i);
		}
		return colors;
	}
}
