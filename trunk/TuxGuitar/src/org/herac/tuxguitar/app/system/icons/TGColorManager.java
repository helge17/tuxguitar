package org.herac.tuxguitar.app.system.icons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.app.system.properties.TGPropertiesUIUtil;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGColorManager {
	
	public static final String COLOR_WHITE = "white";
	public static final String COLOR_BLACK = "black";
	public static final String COLOR_GRAY = "gray";
	public static final String COLOR_BLUE = "blue";
	public static final String COLOR_RED = "red";
	public static final String COLOR_DARK_RED = "darkRed";
	
	private TGContext context;
	private Map<String, UIColor> colors;
	private List<TGSkinnableColor> skinnableColors;
	
	private TGColorManager(TGContext context){
		this.context = context;
		this.colors = new HashMap<String, UIColor>();
		this.skinnableColors = new ArrayList<TGSkinnableColor>();
		this.appendStaticColors();
	}

	private void appendStaticColors() {
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		
		this.colors.put(COLOR_WHITE, uiFactory.createColor(0xff, 0xff, 0xff));
		this.colors.put(COLOR_BLACK, uiFactory.createColor(0x00, 0x00, 0x00));
		this.colors.put(COLOR_GRAY, uiFactory.createColor(0xc0, 0xc0, 0xc0));
		this.colors.put(COLOR_BLUE, uiFactory.createColor(0x00, 0x00, 0xff));
		this.colors.put(COLOR_RED, uiFactory.createColor(0xff, 0x00, 0x00));
		this.colors.put(COLOR_DARK_RED, uiFactory.createColor(0x80, 0x00, 0x00));
	}
	
	public void appendSkinnableColors(TGSkinnableColor[] skinnableColors) {
		TGProperties skinProperties = TGSkinManager.getInstance(this.context).getCurrentSkinProperties();
		
		for(TGSkinnableColor skinnableColor : skinnableColors) {
			this.appendSkinnableColor(skinnableColor, skinProperties);
		}
	}
	
	public void appendSkinnableColor(TGSkinnableColor skinnableColor, TGProperties skinProperties) {
		if(!this.skinnableColors.contains(skinnableColor)) {
			this.skinnableColors.add(skinnableColor);
			this.updateSkinnableColor(skinnableColor, skinProperties);
		}
	}
	
	public void updateSkinnableColor(TGSkinnableColor skinnableColor, TGProperties skinProperties) {
		UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		UIColor uiColor = uiFactory.createColor(TGPropertiesUIUtil.getColorModelValue(skinProperties, skinnableColor.getColorId(), skinnableColor.getDefaultModel()));
		
		if( this.colors.containsKey(skinnableColor.getColorId())) {
			this.colors.remove(skinnableColor.getColorId()).dispose();
		}
		this.colors.put(skinnableColor.getColorId(), uiColor);
	}
	
	public void updateSkinnableColors() {
		TGProperties skinProperties = TGSkinManager.getInstance(this.context).getCurrentSkinProperties();
		
		for(TGSkinnableColor skinnableColor : this.skinnableColors) {
			this.updateSkinnableColor(skinnableColor, skinProperties);
		}
	}
	
	public void onSkinChange() {
		this.updateSkinnableColors();
	}
	
	public void onSkinDisposed() {
		this.disposeColors();
	}
	
	public void disposeColors() {
		if( this.colors != null ) {
			for(UIColor uiColor : this.colors.values()) {
				uiColor.dispose();
			}
			this.colors.clear();
		}
	}
	
	public UIColor getColor(String colorId) {
		return this.colors.get(colorId);
	}
	
	public static TGColorManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGColorManager.class.getName(), new TGSingletonFactory<TGColorManager>() {
			public TGColorManager createInstance(TGContext context) {
				return new TGColorManager(context);
			}
		});
	}
	
	public static class TGSkinnableColor {
		
		private String colorId;
		private UIColorModel defaultModel;
		
		public TGSkinnableColor(String colorId, UIColorModel defaultModel) {
			this.colorId = colorId;
			this.defaultModel = defaultModel;
		}

		
		public String getColorId() {
			return colorId;
		}

		
		public UIColorModel getDefaultModel() {
			return defaultModel;
		}
		
		public boolean equals(Object obj) {
			if( obj instanceof TGSkinnableColor ) {
				return (this.getColorId() != null && this.getColorId().equals(((TGSkinnableColor) obj).getColorId()));
			}
			return super.equals(obj);
		}
	}
}
