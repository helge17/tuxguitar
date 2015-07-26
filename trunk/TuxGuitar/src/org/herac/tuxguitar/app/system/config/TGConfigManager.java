package org.herac.tuxguitar.app.system.config;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGConfigManager extends org.herac.tuxguitar.util.configuration.TGConfigManager{
	
	public static final String CONFIGURATION_MODULE = "tuxguitar";
	
	public TGConfigManager(TGContext context){
		super(context, CONFIGURATION_MODULE);
	}
	
	public void setValue(String key,RGB rgb){
		this.setValue(key,(rgb.red + "," + rgb.green + "," + rgb.blue));
	}
	
	public void setValue(String key,FontData fd){
		this.setValue(key,(fd.getName() + "," + fd.getHeight() + "," + fd.getStyle()));
	}
	
	public FontData getFontDataConfigValue(String key){
		try{
			String value = getProperties().getValue(key);
			if(value != null){
				String[] values = value.trim().split(",");
				if(values != null && values.length == 3){
					try{
						String name = values[0].trim();
						int size = Integer.parseInt(values[1].trim());
						int style = Integer.parseInt(values[2].trim());
						return new FontData( (name == null ? "" : name),size,style);
					}catch(NumberFormatException e){
						e.printStackTrace();
					}
				}
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return new FontData();
	}
	
	public RGB getRGBConfigValue(String key){
		try{
			String value = getProperties().getValue(key);
			if(value != null){
				String[] values = value.trim().split(",");
				if(values != null && values.length == 3){
					try{
						int red = Integer.parseInt(values[0].trim());
						int green = Integer.parseInt(values[1].trim());
						int blue = Integer.parseInt(values[2].trim());
						
						return new RGB(red,green,blue);
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
	
	public TGFontModel getFontModelConfigValue(String key){
		try{
			String value = getProperties().getValue(key);
			if(value != null){
				String[] values = value.trim().split(",");
				if(values != null && values.length == 3){
					try{
						String name = values[0].trim();
						int size = Integer.parseInt(values[1].trim());
						int style = Integer.parseInt(values[2].trim());
						return new TGFontModel( (name == null ? "" : name),size, (style & SWT.BOLD) != 0 , (style & SWT.ITALIC) != 0 );
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
	
	public static TGConfigManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGConfigManager.class.getName(), new TGSingletonFactory<TGConfigManager>() {
			public TGConfigManager createInstance(TGContext context) {
				return new TGConfigManager(context);
			}
		});
	}
}
