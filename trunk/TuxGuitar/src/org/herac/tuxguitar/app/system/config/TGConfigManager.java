package org.herac.tuxguitar.app.system.config;

import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGConfigManager extends org.herac.tuxguitar.util.configuration.TGConfigManager{
	
	public static final String CONFIGURATION_MODULE = "tuxguitar";
	
	public TGConfigManager(TGContext context){
		super(context, CONFIGURATION_MODULE);
	}
	
	public void setValue(String key, TGColorModel model){
		this.setValue(key, (model.getRed() + "," + model.getGreen() + "," + model.getBlue()));
	}
	
	public void setValue(String key, UIColorModel model){
		this.setValue(key, new TGColorModel(model.getRed(), model.getGreen(), model.getBlue()));
	}
	
	public void setValue(String key, TGFontModel fm){
		this.setValue(key, (fm.getName() + "," + fm.getHeight() + "," + fm.isBold() + "," + fm.isItalic()));
	}
	
	public void setValue(String key, UIFontModel fm){
		this.setValue(key, new TGFontModel(fm.getName(), fm.getHeight(), fm.isBold(), fm.isItalic()));
	}
	
	public TGFontModel getFontModelConfigValue(String key){
		try{
			String value = getProperties().getValue(key);
			if(value != null){
				String[] values = value.trim().split(",");
				if(values != null && values.length == 4){
					try{
						String name = values[0].trim();
						float size = Float.valueOf(values[1].trim());
						boolean bold = Boolean.valueOf(values[2].trim());
						boolean italic = Boolean.valueOf(values[3].trim());
						
						return new TGFontModel((name == null ? UIFontModel.DEFAULT_NAME : name), size, bold, italic);
					}catch(NumberFormatException e){
						e.printStackTrace();
					}
				}
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	public UIFontModel getUIFontModelConfigValue(String key){
		TGFontModel fm = this.getFontModelConfigValue(key);
		if( fm != null ) {
			return new UIFontModel(fm.getName(), fm.getHeight(), fm.isBold(), fm.isItalic());
		}
		
		return null;
	}
	
	public TGColorModel getColorModelConfigValue(String key){
		try{
			String value = getProperties().getValue(key);
			if(value != null){
				String[] values = value.trim().split(",");
				if(values != null && values.length == 3){
					try{
						int red = Integer.parseInt(values[0].trim());
						int green = Integer.parseInt(values[1].trim());
						int blue = Integer.parseInt(values[2].trim());
						
						return new TGColorModel(red,green,blue);
					}catch(NumberFormatException e){
						e.printStackTrace();
					}
				}
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	public UIColorModel getUIColorModelConfigValue(String key){
		TGColorModel cm = this.getColorModelConfigValue(key);
		if( cm != null ) {
			return new UIColorModel(cm.getRed(), cm.getGreen(), cm.getBlue());
		}
		
		return null;
	}
	
	public static TGConfigManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGConfigManager.class.getName(), new TGSingletonFactory<TGConfigManager>() {
			public TGConfigManager createInstance(TGContext context) {
				return new TGConfigManager(context);
			}
		});
	}
}
