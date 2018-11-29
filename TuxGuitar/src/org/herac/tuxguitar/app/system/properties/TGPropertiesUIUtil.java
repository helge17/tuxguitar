package org.herac.tuxguitar.app.system.properties;

import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;
import org.herac.tuxguitar.util.properties.TGProperties;

public class TGPropertiesUIUtil {
	
	public static UIFontModel getFontModelValue(TGProperties properties, String key, UIFontModel defaultValue){
		try{
			String value = properties.getValue(key);
			if(value != null){
				String[] values = value.trim().split(",");
				if(values != null && values.length == 4){
					try{
						String name = values[0].trim();
						float size = Float.valueOf(values[1].trim());
						boolean bold = Boolean.valueOf(values[2].trim());
						boolean italic = Boolean.valueOf(values[3].trim());
						
						return new UIFontModel((name == null ? UIFontModel.DEFAULT_NAME : name), size, bold, italic);
					}catch(NumberFormatException e){
						e.printStackTrace();
					}
				}
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return defaultValue;
	}
	
	public static UIFontModel getFontModelValue(TGProperties properties, String key) {
		return TGPropertiesUIUtil.getFontModelValue(properties, key, null);
	}
	
	public static UIColorModel getColorModelValue(TGContext context, TGProperties properties, String key, UIColorModel defaultValue) {
		try{
			String value = properties.getValue(key);
			if( value != null) {
				value = TGExpressionResolver.getInstance(context).resolve(value);
				
				String[] values = value.trim().split(",");
				if(values != null && values.length == 3){
					try{
						int red = Integer.parseInt(values[0].trim());
						int green = Integer.parseInt(values[1].trim());
						int blue = Integer.parseInt(values[2].trim());
						
						return new UIColorModel(red,green,blue);
					}catch(NumberFormatException e){
						e.printStackTrace();
					}
				}
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return defaultValue;
	}
	
	public static UIColorModel getColorModelValue(TGContext context, TGProperties properties, String key) {
		return TGPropertiesUIUtil.getColorModelValue(context, properties, key, null);
	}
	
	public static void setValue(TGProperties properties, String key, UIFontModel value){
		properties.setValue(key, (value.getName() + "," + value.getHeight() + "," + value.isBold() + "," + value.isItalic()));
	}
	
	public static void setValue(TGProperties properties, String key, UIColorModel value){
		properties.setValue(key, (value.getRed() + "," + value.getGreen() + "," + value.getBlue()));
	}
}
