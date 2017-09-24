package org.herac.tuxguitar.app.system.icons;

import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGSkinManager {
	
	private TGContext context;
	
	private TGSkinManager(TGContext context){
		this.context = context;
	}
	
	public String getCurrentSkin() {
		return TGConfigManager.getInstance(this.context).getStringValue(TGConfigKeys.SKIN);
	}
	
	public TGProperties getCurrentSkinInfo() {
		return this.getSkinInfo(this.getCurrentSkin());
	}
	
	public TGProperties getSkinInfo(String skin) {
		TGPropertiesManager propertiesManager =  TGPropertiesManager.getInstance(this.context);
		TGProperties properties = propertiesManager.createProperties();
		propertiesManager.readProperties(properties, TGSkinInfoHandler.RESOURCE, skin);
		
		return properties;
	}
	
	public TGProperties getCurrentSkinProperties() {
		return this.getSkinProperties(this.getCurrentSkin());
	}
	
	public TGProperties getSkinProperties(String skin) {
		TGPropertiesManager propertiesManager =  TGPropertiesManager.getInstance(this.context);
		TGProperties properties = propertiesManager.createProperties();
		propertiesManager.readProperties(properties, TGSkinPropertiesHandler.RESOURCE, skin);
		
		return properties;
	}
	
	public static TGSkinManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGSkinManager.class.getName(), new TGSingletonFactory<TGSkinManager>() {
			public TGSkinManager createInstance(TGContext context) {
				return new TGSkinManager(context);
			}
		});
	}
}
